package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.UserDTO;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.repository.UserRepository;
import com.springapp.stackoverflow.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository =userRepository;
    }

    @Transactional
    public User signup(UserDTO.SignupRequest request) {
        // Check if email or username already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .interestedTopics(request.getInterestedTopics())
                .reputations(0)
                .createdAt(LocalDateTime.now())
                .savedQuestions(new ArrayList<>())
                .build();

        return userRepository.save(user);
    }

    public User login(UserDTO.LoginRequest request) {
        // Check if user exists by username or email
        Optional<User> userOptional = userRepository.findByUsername(request.getUsernameOrEmail());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(request.getUsernameOrEmail());
        }

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with given username or email");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserProfile(Long userId, UserDTO.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfilePic() != null) {
            user.setProfilePic(request.getProfilePic());
        }
        if (request.getInterestedTopics() != null) {
            user.setInterestedTopics(request.getInterestedTopics());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserDTO.PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
