package com.jikim.mycommerce.auth;

import com.jikim.mycommerce.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * CustomOAuth2UserService
 *
 * GitHub OAuth2 로그인 처리
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "github"
        String providerId = oAuth2User.getAttribute("id").toString();
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("login"); // GitHub username (필수값)
        String name = oAuth2User.getAttribute("name"); // 이름 (선택값, null 가능)

        // DB에서 사용자 조회 또는 생성
        User user = userService.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : username); // name이 null이면 username 사용
                    newUser.setRole(UserRole.USER);
                    newUser.setStatus(UserStatus.ACTIVE);
                    return userService.createUser(newUser);
                });

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
