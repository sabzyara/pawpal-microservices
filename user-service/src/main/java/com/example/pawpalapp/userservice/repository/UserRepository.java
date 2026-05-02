package com.example.pawpalapp.userservice.repository;

import com.example.pawpalapp.userservice.model.User;
import com.example.pawpalapp.userservice.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
