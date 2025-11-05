package com.jikim.mycommerce.user;


import com.jikim.mycommerce.auth.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

// TODO 
//  - Reqeuest, Response DTO 적용
//  - 공통 로그, 예외 처리
/**
 * UserController
 *
 * 사용자 정보 API
 *
 * @author wjddl
 * @since 25. 11. 4.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws URISyntaxException {
        User createdUser = userService.createUser(user);
        // WebConfig에서 /api가 자동 추가되므로 하드코딩된 /api 제거
        return ResponseEntity.created(new URI("/users/" + createdUser.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        try {
            List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedinUser(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.ok(UserResponse.unauthenticated());
        }

        return ResponseEntity.ok(
                UserResponse.from(user)
        );
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User foundUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(foundUser);
    }

}
