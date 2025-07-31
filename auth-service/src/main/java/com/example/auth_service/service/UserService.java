package com.example.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;

@Service
public class UserService {
    public UserRepository userRespository;

    public UserService(UserRepository userRepository) {
        this.userRespository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRespository.findByEmail(email);
    }

}
