package com.jikim.mycommerce.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ErrorResponse
 *
 * 예외 응답 DTO
 *
 * @author wjddl
 * @since 25. 11. 5.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
