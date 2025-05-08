package com.superkids.demo_identity.service;

import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.response.UserResponse;
import com.superkids.demo_identity.entity.User;
import com.superkids.demo_identity.exception.AppException;
import com.superkids.demo_identity.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private User user;
    private UserResponse responseUser;
    private LocalDate dob;

    @BeforeEach
    void initData(){
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
