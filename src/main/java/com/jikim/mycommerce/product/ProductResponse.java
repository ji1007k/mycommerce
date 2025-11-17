package com.jikim.mycommerce.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductResponse
 *
 * 상품 응답
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
