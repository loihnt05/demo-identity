package com.superkids.demo_identity.service;

import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.request.UserUpdateRequest;
import com.superkids.demo_identity.dto.response.UserResponse;
import com.superkids.demo_identity.entity.User;
import com.superkids.demo_identity.exception.AppException;
import com.superkids.demo_identity.exception.ErrorCode;
import com.superkids.demo_identity.mapper.UserMapper;
import com.superkids.demo_identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    //create
    public User createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);

        return userRepository.save(user);
    }

    //read all
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    //read
    public UserResponse getUser(String userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found user")));
    }

    //update
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found user"));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    //delete
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
