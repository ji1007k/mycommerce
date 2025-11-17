package com.jikim.mycommerce.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * ProductCreateRequest
 *
 * 상품 생성 요청
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수입니다")
        @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
        String name,

        String description,

        @NotNull(message = "가격은 필수입니다")
        @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
        BigDecimal price,

        @NotNull(message = "재고 수량은 필수입니다")
        @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
        Integer stockQuantity
) {
}
