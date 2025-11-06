package com.jikim.mycommerce.user;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

/**
 * UserRequest
 *
 * 사용자 API 요청용 DTO
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Builder
@Getter
public class UserRequest {
    private String name;
    private String email;
    private String password;

    @Column(length = 11)
    @Pattern(regexp = "^\\d{11}$")    // 숫자 11자리만
    private String phoneNumber;

    private String provider;
    private String providerId;


    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(UserRole.USER)
                .phoneNumber(phoneNumber)
                .provider(provider)
                .providerId(providerId)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
