package com.spotify.demo.service;

import com.spotify.demo.dto.LoginResponse;
import com.spotify.demo.dto.RegisterResponseDTO;
import com.spotify.demo.entity.User;
import com.spotify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponseDTO registerUser(String username, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        Optional<User> existingUsername = userRepository.findByUsername(username);
        if (existingUsername.isPresent()) {
            throw new IllegalArgumentException("Username already present");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return new RegisterResponseDTO(username, email);
    }

    public LoginResponse loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(
                () -> new RuntimeException("User not found")
        );
        String hashPassword = user.getPassword();
        boolean isCorrect = passwordEncoder.matches(password, hashPassword);

        LoginResponse loginResponse = new LoginResponse();
        if (isCorrect) {
            loginResponse.setResponse("Welcome back! ");
        }
        else {
            loginResponse.setResponse("Wrong Password");
        }
        return loginResponse;
    }
}
