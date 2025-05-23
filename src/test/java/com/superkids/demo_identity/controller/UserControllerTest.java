package com.superkids.demo_identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.response.UserResponse;
import com.superkids.demo_identity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(UserController.class)
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse response;

    @BeforeEach
    void initData(){
        LocalDate dob = LocalDate.of(1999, 12, 12);

        request = UserCreationRequest.builder()
                .username("test")
                .password("test123456")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();

        response = UserResponse.builder()
                .id("sdfhdfhbrtjrt")
                .username("test")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();

    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(userService.createUser(any())).thenReturn(response);
        //THEN, WHEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id")
                        .value("sdfhdfhbrtjrt")
        );
    }
    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        //GIVEN
        request.setUsername("te");
        String content = objectMapper.writeValueAsString(request);

        //THEN, WHEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Username must contain at least 3 characters")
                );
    }

}
