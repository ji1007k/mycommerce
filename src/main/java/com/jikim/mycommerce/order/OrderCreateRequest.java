package com.jikim.mycommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * OrderCreateRequest
 *
 * 주문 생성 요청
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public record OrderCreateRequest(
        @NotEmpty(message = "주문 항목은 필수입니다")
        @Valid
        List<OrderItemRequest> items
) {
}
