package com.jikim.mycommerce.user;


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

    // 전체 사용자 목록 조회
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
//        If a value is present, returns the value, otherwise throws NoSuchElementException
        return userRepository.findByEmail(email).orElseThrow(); //
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }
}
