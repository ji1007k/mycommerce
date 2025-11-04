package com.jikim.mycommerce.user;


import com.jikim.mycommerce.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * User
 *
 * 사용자 정보
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@Entity
@Table(name = "users")
@RequiredArgsConstructor
@Getter @Setter
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(length = 11)
    @Pattern(regexp = "^\\d{11}$")    // 숫자 11자리만
    private String phoneNumber;

    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    // created_at, updated_at 필드는 JPA Auditing에 의해 자동 생성&관리됨
}
