package com.jikim.mycommerce.order;

import com.jikim.mycommerce.fixture.TestDataFactory;
import com.jikim.mycommerce.product.Product;
import com.jikim.mycommerce.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OrderRepositoryTest
 *
 * 주문 레포지토리 테스트
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@DataJpaTest     // 기본적으로 @Transactional + @Rollback
@EnableJpaAuditing  // JPA Auditing 활성화
@DisplayName("OrderRepository 테스트")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("주문을 저장한다")
    void save() {
        // given
        TestDataFactory.OrderWithDependencies testData = 
                TestDataFactory.savedOrderWithDependencies(entityManager, 3);

        // when
        entityManager.flush();
        entityManager.clear();

        // then
        Order savedOrder = testData.order();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        assertThat(savedOrder.getOrderItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(savedOrder.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("사용자 ID로 모든 주문을 조회한다 (N+1 방지)")
    void findAllByUserIdWithItems() {
        // given
        User savedUser = TestDataFactory.savedUser(entityManager);
        
        Product savedProduct1 = TestDataFactory.savedProduct(entityManager,
                TestDataFactory.product()
                        .name("상품1")
                        .price(BigDecimal.valueOf(10000))
                        .stock(100)
        );
        
        Product savedProduct2 = TestDataFactory.savedProduct(entityManager,
                TestDataFactory.product()
                        .name("상품2")
                        .price(BigDecimal.valueOf(20000))
                        .stock(50)
        );

        Order savedOrder1 = TestDataFactory.savedCompleteOrder(entityManager, savedUser, savedProduct1, 3);
        Order savedOrder2 = TestDataFactory.savedCompleteOrder(entityManager, savedUser, savedProduct2, 2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Order> orders = orderRepository.findAllByUserIdWithItems(savedUser.getId());

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getOrderItems()).isNotEmpty();
        assertThat(orders.get(1).getOrderItems()).isNotEmpty();
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회한다 (N+1 방지)")
    void findByIdWithItems() {
        // given
        User savedUser = TestDataFactory.savedUser(entityManager);
        Product savedProduct = TestDataFactory.savedProduct(entityManager);
        Order savedOrder = TestDataFactory.savedCompleteOrder(entityManager, savedUser, savedProduct, 3);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Order> foundOrder = orderRepository.findByIdWithItems(savedOrder.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderItems()).hasSize(1);
        assertThat(foundOrder.get().getOrderItems().get(0).getProduct().getName())
                .isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("주문 상태를 변경한다")
    void changeStatus() {
        // given
        TestDataFactory.OrderWithDependencies testData = 
                TestDataFactory.savedOrderWithDependencies(entityManager, 3);
        Long orderId = testData.order().getId();

        entityManager.flush();
        entityManager.clear();

        // when
        Order foundOrder = orderRepository.findById(orderId).orElseThrow();
        foundOrder.changeStatus(OrderStatus.PAID);
        entityManager.flush();
        entityManager.clear();

        // then
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("Cascade로 OrderItem이 함께 삭제된다")
    void cascadeDelete() {
        // given
        TestDataFactory.OrderWithDependencies testData = 
                TestDataFactory.savedOrderWithDependencies(entityManager, 3);
        Long orderId = testData.order().getId();

        entityManager.flush();
        entityManager.clear();

        // when
        orderRepository.deleteById(orderId);
        entityManager.flush();

        // then
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertThat(deletedOrder).isEmpty();
    }

    @Test
    @DisplayName("Optimistic Lock 버전이 자동으로 증가한다")
    void optimisticLock() {
        // given
        TestDataFactory.OrderWithDependencies testData = 
                TestDataFactory.savedOrderWithDependencies(entityManager, 1);
        Order savedOrder = testData.order();
        Long initialVersion = savedOrder.getVersion();
        entityManager.clear();

        // when
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        foundOrder.changeStatus(OrderStatus.PAID);
        entityManager.flush();
        entityManager.clear();

        // then
        Order updatedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertThat(updatedOrder.getVersion()).isGreaterThan(initialVersion);
    }

    @Test
    @DisplayName("커스텀 주문 상태로 저장한다")
    void saveWithCustomStatus() {
        // given - Builder 방식의 유연성 활용
        User savedUser = TestDataFactory.savedUser(entityManager,
                TestDataFactory.user()
                        .name("VIP고객")
                        .email("vip@example.com")
        );

        Product savedProduct = TestDataFactory.savedProduct(entityManager,
                TestDataFactory.product()
                        .name("프리미엄상품")
                        .price(BigDecimal.valueOf(100000))
                        .stock(10)
        );

        Order savedOrder = TestDataFactory.savedOrder(entityManager,
                TestDataFactory.order()
                        .user(savedUser)
                        .status(OrderStatus.PAID)  // 커스텀 상태
                        .totalPrice(BigDecimal.valueOf(200000))
        );

        entityManager.flush();
        entityManager.clear();

        // when
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();

        // then
        assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(foundOrder.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(200000));
        assertThat(foundOrder.getUser().getName()).isEqualTo("VIP고객");
    }
}