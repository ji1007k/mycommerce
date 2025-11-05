package com.jikim.mycommerce.auth;

import com.jikim.mycommerce.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * CustomOAuth2User
 *
 * Spring Security의 OAuth2User 구현체
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getName() {
        if (user.getName() != null) {
            return user.getName();
        }

        // GitHub login(username)을 principalName으로 사용
        return user.getProvider() + "_" + user.getProviderId();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getRole() {
        return user.getRole().name();
    }
}
