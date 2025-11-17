package com.jikim.mycommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository
 *
 * 주문 레포지토리
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 사용자 ID로 모든 주문을 조회한다
     * N+1 문제 방지를 위한 fetch join 사용
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.product " +
           "WHERE o.user.id = :userId")
    List<Order> findAllByUserIdWithItems(@Param("userId") Long userId);

    /**
     * 주문 ID로 주문을 조회한다
     * N+1 문제 방지를 위한 fetch join 사용
     *
     * @param id 주문 ID
     * @return 주문
     */
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.product " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
