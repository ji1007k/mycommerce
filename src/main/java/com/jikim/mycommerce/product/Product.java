package com.jikim.mycommerce.product;

import com.jikim.mycommerce.common.domain.BaseTimeEntity;
import com.jikim.mycommerce.common.exception.InsufficientStockException;
import com.jikim.mycommerce.common.exception.InvalidQuantityException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product
 *
 * 상품 정보
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Entity
@Table(name = "products")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Version
    private Long version;


    /**
     * 상품 정보를 수정한다
     *
     * @param name 상품명
     * @param description 상품 설명
     * @param price 가격
     */
    public void updateInfo(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * 재고를 감소시킨다
     *
     * @param quantity 감소시킬 수량
     * @throws InvalidQuantityException 수량이 0 이하인 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public void decreaseStock(Integer quantity) {
        // 1. 수량 검증
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }

        // 2. 재고 부족 검증
        if (this.stock < quantity) {
            throw new InsufficientStockException(this.id, quantity, this.stock);
        }

        // 3. 재고 차감
        this.stock -= quantity;
    }

    /**
     * 재고를 증가시킨다
     *
     * @param quantity 증가시킬 수량
     * @throws InvalidQuantityException 수량이 0 이하인 경우
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }

        this.stock += quantity;
    }
}
