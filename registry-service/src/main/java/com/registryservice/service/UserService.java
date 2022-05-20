package com.registryservice.service;

import com.registryservice.dto.SignUpDto;
import com.registryservice.model.User;
import com.registryservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<User> findAll() {
        log.info("Inside findAll");
        return this.userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return this.userRepository.findByUsername(username);
    }

    public User registerUser(SignUpDto signUpDto) throws Exception {
        log.info("Inside registerUser {}", signUpDto.username);
        if (this.userRepository.existsByUsername(signUpDto.username)) {
            log.warn("username {} already exists", signUpDto.username);

            throw new Exception(String.format("username %s already exists", signUpDto.username));
        }

        if (this.userRepository.existsByEmail(signUpDto.email)) {
            log.warn("email {} already exists", signUpDto.email);

            throw new Exception(String.format("email %s already exists", signUpDto.email));
        }
        User user = new User(signUpDto.getUsername(), passwordEncoder.encode(signUpDto.getPassword()), signUpDto.getEmail());
        return this.userRepository.save(user);
    }
}
