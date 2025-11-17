package com.jikim.mycommerce.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * OrderItemRequest
 *
 * 주문 항목 요청
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public record OrderItemRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @NotNull(message = "수량은 필수입니다")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다")
        Integer quantity
) {
}
