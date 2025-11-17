package com.jikim.mycommerce.order;

import com.jikim.mycommerce.product.Product;
import com.jikim.mycommerce.product.ProductRepository;
import com.jikim.mycommerce.product.ProductStatus;
import com.jikim.mycommerce.user.User;
import com.jikim.mycommerce.user.UserRepository;
import com.jikim.mycommerce.user.UserRole;
import com.jikim.mycommerce.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OrderConcurrencyTest
 *
 * 주문 동시성 테스트
 * Redis 분산 락 + 낙관적 락 검증
 *
 * @author wjddl
 * @since 25. 11. 17.
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성
        testUser = User.builder()
                .name("동시성테스트유저")
                .email("concurrent@test.com")
                .phoneNumber("01099999999")
                .provider("github")
                .providerId("github_999")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.saveAndFlush(testUser);

        // 테스트 상품 생성 (재고 10개)
        testProduct = Product.builder()
                .name("동시성 테스트 상품")
                .description("재고 10개로 동시성 테스트")
                .price(BigDecimal.valueOf(10000))
                .stock(10)  // 재고 10개
                .status(ProductStatus.AVAILABLE)
                .build();
        productRepository.saveAndFlush(testProduct);
    }

    @Test
    @DisplayName("10명이 동시에 5개씩 주문 → 2명만 성공해야 함")
    void concurrentOrderTest_10Users_5Each() throws InterruptedException {
        // given
        int threadCount = 10;
        int orderQuantity = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 10명이 동시에 주문
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderItemRequest itemRequest = new OrderItemRequest(testProduct.getId(), orderQuantity);
                    OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

                    orderService.createOrder(testUser.getId(), request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("주문 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(2);  // 2명만 성공
        assertThat(failCount.get()).isEqualTo(8);     // 8명 실패

        // 재고 확인
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(0);  // 재고 소진
    }

    @Test
    @DisplayName("100명이 동시에 1개씩 주문 → 10명만 성공해야 함")
    void concurrentOrderTest_100Users_1Each() throws InterruptedException {
        // given
        int threadCount = 100;
        int orderQuantity = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderItemRequest itemRequest = new OrderItemRequest(testProduct.getId(), orderQuantity);
                    OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

                    orderService.createOrder(testUser.getId(), request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(10);  // 10명만 성공
        assertThat(failCount.get()).isEqualTo(90);     // 90명 실패

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(0);
    }



    @Test
    @DisplayName("여러 상품 동시 주문 테스트 - 2-Phase 검증으로 개선")
    void concurrentOrderTest_MultipleProducts() throws InterruptedException {
        // given
        Product product2 = Product.builder()
                .name("상품2")
                .description("재고 5개")
                .price(BigDecimal.valueOf(20000))
                .stock(5)
                .status(ProductStatus.AVAILABLE)
                .build();
        productRepository.saveAndFlush(product2);

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 두 상품을 동시에 주문
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    List<OrderItemRequest> items = List.of(
                            new OrderItemRequest(testProduct.getId(), 1),  // 상품1: 1개 (재고 10)
                            new OrderItemRequest(product2.getId(), 1)      // 상품2: 1개 (재고 5)
                    );
                    OrderCreateRequest request = new OrderCreateRequest(items);

                    orderService.createOrder(testUser.getId(), request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("성공한 주문 수: " + successCount.get());
        System.out.println("실패한 주문 수: " + failCount.get());

        // 2-Phase 검증 덕분에:
        // - Phase 1에서 재고 부족 감지 시 즉시 실패 (재고 차감 안함)
        // - Phase 2에서만 실제 재고 차감
        // → 상품2 재고가 5개이므로 정확히 5개 주문만 성공
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(15);

        Product updatedProduct1 = productRepository.findById(testProduct.getId()).orElseThrow();
        Product updatedProduct2 = productRepository.findById(product2.getId()).orElseThrow();

        System.out.println("상품1 남은 재고: " + updatedProduct1.getStock());
        System.out.println("상품2 남은 재고: " + updatedProduct2.getStock());

        // 2-Phase 검증으로 정확한 재고 관리!
        assertThat(updatedProduct1.getStock()).isEqualTo(5);   // 10 - 5 = 5 (정확히 5개만 차감)
        assertThat(updatedProduct2.getStock()).isEqualTo(0);   // 5 - 5 = 0
    }
}
