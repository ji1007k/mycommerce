package com.jikim.mycommerce.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * ProductRepository
 *
 * 상품 레포지토리
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 비관적 락으로 상품 조회
     * 동시성 제어가 필요한 재고 업데이트 시 사용
     *
     * @param productId 상품 ID
     * @return 상품
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithPessimisticLock(Long productId);

}
