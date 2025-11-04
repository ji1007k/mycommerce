package com.jikim.mycommerce.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JpaConfig
 *
 * @author wjddl
 * @since 2025-11-04
 */
@Configuration
@EnableJpaAuditing  // JPA Auditing 활성화. 엔티티 생성/수정 시 시간, 작성자 자동 기록
public class JpaConfig {}
