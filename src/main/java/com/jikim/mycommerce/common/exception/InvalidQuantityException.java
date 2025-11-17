package com.jikim.mycommerce.common.exception;

/**
 * InvalidQuantityException
 *
 * 잘못된 수량 예외
 *
 * @author wjddl
 * @since 25. 11. 17.
 */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String message) {
        super(message);
    }

    public InvalidQuantityException(Integer quantity) {
        super(String.format("잘못된 수량: %d (수량은 1 이상이어야 합니다)", quantity));
    }
}
