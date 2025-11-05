package com.jikim.mycommerce.auth;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CsrfController
 *
 * CSRF 토큰 제공 엔드포인트
 *
 * @author wjddl
 * @since 25. 11. 5.
 */
@RestController
@RequestMapping("/csrf")
public class CsrfController {

    @GetMapping
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
