package com.jikim.mycommerce.common.config;

import com.jikim.mycommerce.auth.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.function.Supplier;

/**
 * SecurityConfig
 *
 * Spring Security 설정
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    @Profile({"dev", "test"})
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight 허용
                        .requestMatchers(
                            "/oauth2/**",
                            "/login/**",
                            "/api/csrf"     // WebConfig에서 /api 추가되므로 /api/csrf로 매칭 필요
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Security Filter Chain 레벨 예외 처리
                // Controller 진입 전, 인증 실패 시
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // /logout 요청은 예외 처리 안 함
                            if (request.getRequestURI().equals("/logout")) {
                                return;
                            }
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        })
                )

                // 로그인 설정
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/oauth2/authorization/github")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 후 메인으로
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")  // 세션, CSRF 토큰 삭제
                        .invalidateHttpSession(true)
                )
                
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
        
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight 허용
                        .requestMatchers(
                            "/oauth2/**",
                            "/login/**",
                            "/api/csrf"     // WebConfig에서 /api 추가되므로 /api/csrf로 매칭 필요
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Security Filter Chain 레벨 예외 처리
                // Controller 진입 전, 인증 실패 시
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // /logout 요청은 예외 처리 안 함
                            if (request.getRequestURI().equals("/logout")) {
                                return;
                            }
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        })
                )

                // 로그인 설정
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/oauth2/authorization/github")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 후 메인으로
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")  // 세션, CSRF 토큰 삭제
                        .invalidateHttpSession(true)
                )

                // CSRF 설정
//                .csrf(csrf -> csrf.disable())
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // SPA용 CSRF 처리기 (fetch 요청 시 X-XSRF-TOKEN 헤더 지원)
    // XOR 마스킹 적용으로 BREACH 공격 완화
    final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        // SPA: 페이지 새로고침 없이 fetch 요청 -> CsrfTokenRequestHandler 정상 동작x
//        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();   // CSRF 토큰을 그대로 사용

        // XOR 기반 토큰 암호화 핸들러 (Spring Security 6 기본)
        // BREACH 공격 방어 목적
        //  HTTPS 압축 취약점을 이용한 토큰 값 유추 공격
        //  XOR로 매번 다른 값 만들면 방어 가능
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
            // 실제 요청 처리 시 XOR 토큰 적용 (BREACH 공격 방어)
            this.xor.handle(request, response, csrfToken);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8090");   // 개발 중인 프론트엔드 출처만 허용 (CORS 보안 유지)
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);   // 브라우저가 credentials: 'include' 로 쿠키(JSESSIONID, XSRF-TOKEN) 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);    // 모든 엔드포인트에 적용
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
//        FilterChain 진입 전 차단 (DispatcherServlet 이전)
        return web -> web.ignoring()    // 시큐리티 필터 체인 우회
                // 정적 리소스 허용
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**");
    }


}
