package com.jikim.mycommerce.order;

import com.jikim.mycommerce.common.domain.BaseTimeEntity;
import com.jikim.mycommerce.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Order
 *
 * 주문 정보
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Entity
@Table(name = "orders")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
// @EntityGraph는 fetch join을 어노테이션 방식으로 설정
@NamedEntityGraph(  // 재사용 가능한 그래프
        name = "Order.withUser",
        attributeNodes = @NamedAttributeNode("user")
)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Version
    private Long version;

    /**
     * 주문 항목을 추가한다
     *
     * @param orderItem 주문 항목
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 주문 상태를 변경한다
     *
     * @param status 변경할 상태
     */
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 결제를 처리한다
     * Mock 결제 게이트웨이 연동 로직 구현
     */
    public void processPayment() {
        // TODO: Mock payment gateway 연동 구현
    }

    /**
     * 주문을 취소한다
     * 재고 복원 로직 구현
     */
    public void cancel() {
        // TODO: 주문 취소 로직 구현
        // 1. 주문 상태 검증 (PENDING, PAYMENT_COMPLETED만 취소 가능)
        // 2. 재고 복원
        // 3. 환불 처리 (결제 완료된 경우)
        // 4. 상태를 CANCELLED로 변경
    }

}
