package com.jikim.mycommerce.common.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 *
 * Spring MVC 전역 설정
 * 
 * API 경로 자동 prefix 설정: 모든 @RestController에 "/api" 자동 추가
 * 정적 리소스(@Controller)는 영향 없음
 *
 * @author wjddl
 * @since 25. 11. 5.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * REST API 경로에 "/api" prefix 자동 추가
     * @param configurer 경로 매칭 설정 객체
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api",
                c -> c.isAnnotationPresent(RestController.class));
    }
}
