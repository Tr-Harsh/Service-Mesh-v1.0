package com.registryservice.controller;

import com.registryservice.dto.JWTAuthResponseDto;
import com.registryservice.dto.SignInDto;
import com.registryservice.dto.SignUpDto;
import com.registryservice.model.User;
import com.registryservice.security.JWTTokenProvider;
import com.registryservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInDto signInDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDto.getUsername(),
                        signInDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String generatedToken = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTAuthResponseDto(generatedToken));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpDto signUpDto) {
        log.info("Inside createUser: " + signUpDto.username);
        try {
            User savedUser = this.userService.registerUser(signUpDto);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        log.info("retrieving all users");
        List<User> allUsers = userService.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.FOUND);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        log.info("retrieving all users");
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) return new ResponseEntity<>(user.get(), HttpStatus.FOUND);
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
