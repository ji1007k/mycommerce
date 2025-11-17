package com.jikim.mycommerce.order;

import com.jikim.mycommerce.common.domain.BaseTimeEntity;
import com.jikim.mycommerce.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem
 *
 * 주문 항목 정보
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Entity
@Table(name = "order_items")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 주문 항목의 총 가격을 계산한다
     *
     * @return 총 가격
     */
    public BigDecimal calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
