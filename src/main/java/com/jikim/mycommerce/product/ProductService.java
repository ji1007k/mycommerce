package com.jikim.mycommerce.product;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ProductService
 *
 * 상품 서비스
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    /**
     * 상품을 생성한다
     *
     * @param request 상품 생성 요청
     * @return 생성된 상품
     */
    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stockQuantity())
                .status(ProductStatus.AVAILABLE)
                .build();

        return productRepository.save(product);
    }

    /**
     * 상품 재고를 차감한다
     *
     * 주의: 동시성 제어는 Service 계층에서 처리!
     * - Redis 분산 락 (ProductService)        ← Service 책임
     * - DB Pessimistic Lock (Repository)      ← Repository 책임
     * - Optimistic Lock (@Version 자동 처리)   ← JPA 책임
     *
     * @param productId 상품ID
     * @param quantity  주문 개수
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // ← 별도 트랜잭션으로 관리
    public void decreaseStockWithLock(Long productId, Integer quantity) {
        RLock lock = redissonClient.getLock("lock:product:" + productId);

        try {
            // Redis distributed lock으로 동시성 제어
            boolean acquired = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("재고 락 획득 실패");
            }

            // Pessimistic Lock으로 조회
            Product product = productRepository
                    .findByIdWithPessimisticLock(productId)
                    .orElseThrow();

            // Entity의 순수 로직 호출
            product.decreaseStock(quantity);

            // @Version으로 Optimistic Lock 자동 체크
            productRepository.save(product);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재고 차감 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();  // ← 커밋 후 락 해제
            }
        }
    }

    /**
     * 상품을 조회한다
     *
     * @param id 상품 ID
     * @return 상품
     */
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    /**
     * 모든 상품을 조회한다
     *
     * @return 상품 목록
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 상품 정보를 수정한다
     *
     * @param id 상품 ID
     * @param request 상품 수정 요청
     * @return 수정된 상품
     */
    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findProductById(id);
        product.updateInfo(request.name(), request.description(), request.price());
        return product;
    }

    /**
     * 상품을 삭제한다
     *
     * @param id 상품 ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
