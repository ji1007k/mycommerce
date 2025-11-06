package com.jikim.mycommerce.user;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// TODO MapStruct 적용
/**
 * UserService
 *
 * 사용자 서비스.
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByEmail(String email) {
//        If a value is present, returns the value, otherwise throws NoSuchElementException
        return userRepository.findByEmail(email)
                // Optional을 내부에서 orElseThrow()로 처리
                .orElseThrow(() -> new EntityNotFoundException("User not found. Email: " + email));
    }

    // 소셜 로그인 인증제공자 및 소셜 로그인 ID로 사용자 조회
    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }
}
