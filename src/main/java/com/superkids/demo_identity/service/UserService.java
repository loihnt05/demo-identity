package com.superkids.demo_identity.service;

import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.request.UserUpdateRequest;
import com.superkids.demo_identity.entity.User;
import com.superkids.demo_identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //create
    public User createRequest(UserCreationRequest request){
        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    //read all
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    //read
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found user"));
    }

    //update
    public User updateUser(String userId, UserUpdateRequest request){
        User user = getUser(userId);

        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    //delete
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
