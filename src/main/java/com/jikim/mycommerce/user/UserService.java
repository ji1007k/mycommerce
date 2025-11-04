package com.jikim.mycommerce.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
