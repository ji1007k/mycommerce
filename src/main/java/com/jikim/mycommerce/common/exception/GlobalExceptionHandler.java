package com.jikim.mycommerce.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * GlobalExceptionHandler
 *
 * 컨트롤러 레벨에서 발생한 예외를 처리하고 JSON 응답으로 변환
 * 처리 범위:
 *   - @Controller, @RestController 메서드 내부 예외
 *   - 서비스 레이어에서 전파된 예외
 * 처리 불가:
 *   - DispatcherServlet 이전 단계 예외 (Filter, Interceptor)
 *   - Spring Security FilterChain 예외 (별도 처리 필요)
 *
 * @author wjddl
 * @since 25. 11. 5.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("리소스 없음: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", "리소스를 찾을 수 없습니다."));
    }

    // 비즈니스 예외 (커스텀)
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
//        log.error("비즈니스 예외: {}", e.getMessage());
//        return ResponseEntity.status(e.getStatus())
//                .body(new ErrorResponse(e.getCode(), e.getMessage()));
//    }

    // 모든 예외 (맨 마지막 순서로 선언 필수)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 에러: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."));
    }
}
