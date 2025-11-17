package com.jikim.mycommerce.order;

import java.math.BigDecimal;

/**
 * OrderItemResponse
 *
 * 주문 항목 응답
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.calculateTotalPrice()
        );
    }
}
