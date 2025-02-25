package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.UserDTO;
import com.springapp.stackoverflow.model.User;

public interface UserService {
    User signup(UserDTO.SignupRequest request);
    User login(UserDTO.LoginRequest request);
    User getUserById(Long userId);
    User updateUserProfile(Long userId, UserDTO.UserUpdateRequest request);
    void updatePassword(Long userId, UserDTO.PasswordUpdateRequest request);
}
