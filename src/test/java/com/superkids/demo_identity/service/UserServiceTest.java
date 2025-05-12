package com.superkids.demo_identity.service;

import com.superkids.demo_identity.DemoIdentityApplication;
import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.response.UserResponse;
import com.superkids.demo_identity.entity.User;
import com.superkids.demo_identity.exception.AppException;
import com.superkids.demo_identity.mapper.UserMapper;
import com.superkids.demo_identity.repository.RoleRepository;
import com.superkids.demo_identity.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserService userService;

    private UserCreationRequest request;
    private User user;
    private UserResponse responseUser;
    private LocalDate dob;

    @BeforeEach
    void initData(){
        userService = new UserService(userRepository, roleRepository, userMapper, passwordEncoder);
        dob = LocalDate.of(1999, 12, 12);

        request = UserCreationRequest.builder()
                .username("test")
                .password("test123456")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();

        responseUser = UserResponse.builder()
                .id("sdfhdfhbrtjrt")
                .username("test")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();

        user = User.builder()
                .id("sdfhdfhbrtjrt")
                .username("test")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success(){
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        //WHEN
        var response = userService.createUser(request);

        //Then
        Assertions.assertThat(response.getId()).isEqualTo(responseUser.getId());
        Assertions.assertThat(response.getUsername()).isEqualTo(responseUser.getUsername());
    }

    @Test
    void createUser_userExisted_fail(){
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        //WHEN
        var exception = assertThrows(AppException.class,
                () -> userService.createUser(request)
                );
        Assertions.assertThat(exception.getErrorCode().getCode())
                .isEqualTo(1001);
    }
}
