package com.jikim.mycommerce.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void createUser() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("username").build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> users = userService.findAllUsers();
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(2);
        verify(userRepository).findAll(); // 호출 여부 검증
    }

    @Test
    void findUserByEmail() {
        Long userId = 1L;

        User user = User.builder().id(userId)
                .email("test@email.com").build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail("email");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
        assertThat(foundUser.getEmail()).isEqualTo("test@email.com");
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    void findUserByEmail_EntityNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail("notFound@email.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByProviderAndProviderId() {
        Long userId = 1L;

        User user = User.builder().id(userId)
                .provider("GITHUB")
                .providerId("providerId").build();

        when(userRepository.findByProviderAndProviderId(anyString(), anyString())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByProviderAndProviderId("providerId", "providerId");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getProvider()).isEqualTo("GITHUB");
        assertThat(foundUser.get().getProviderId()).isEqualTo("providerId");
        verify(userRepository).findByProviderAndProviderId(anyString(), anyString());
    }
}