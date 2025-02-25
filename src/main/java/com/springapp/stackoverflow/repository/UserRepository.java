package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String name);
}
