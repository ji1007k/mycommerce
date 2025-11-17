package com.jikim.mycommerce.common.exception;

/**
 * InsufficientStockException
 *
 * 재고 부족 예외
 *
 * @author wjddl
 * @since 25. 11. 17.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super(String.format("재고 부족: 상품 ID=%d, 요청=%d, 현재 재고=%d", productId, requested, available));
    }
}
