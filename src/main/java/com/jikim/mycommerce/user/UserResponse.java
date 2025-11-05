package com.jikim.mycommerce.user;


import com.jikim.mycommerce.auth.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * UserResponse
 *
 * 사용자 API 응답용 DTO
 *
 * @author wjddl
 * @since 25. 11. 5.
 */
@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private boolean authenticated;
    private Long userId;
    private String name;
    private String email;
    private String role;

    public static UserResponse from(CustomOAuth2User user) {
        return UserResponse.builder()
                .authenticated(true)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getUser().getEmail())
                .role(user.getRole())
                .build();
    }

    public static UserResponse unauthenticated() {
        return UserResponse.builder()
                .authenticated(false)
                .build();
    }
}
