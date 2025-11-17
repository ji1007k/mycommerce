package com.jikim.mycommerce.common.domain;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseTimeEntity
 *
 * 엔티티 생성/수정일시 자동 관리.
 * JPA(도메인) 계층에서만 사용
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@MappedSuperclass   // 엔티티(Entity)들의 공통 필드(컬럼)를 모아놓는 '상속용 템플릿' 클래스. 테이블로 생성되지 x
@EntityListeners(AuditingEntityListener.class)  // Auditing 리스너 적용
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
