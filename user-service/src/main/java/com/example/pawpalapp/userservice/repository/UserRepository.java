package com.example.pawpalapp.userservice.repository;

import com.example.pawpalapp.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
