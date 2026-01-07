package com.example.pawpalapp.userservice.model;

import com.example.pawpalapp.userservice.model.enums.Role;
import com.example.pawpalapp.userservice.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="user_seq_gen")
    @SequenceGenerator(allocationSize=1, schema="public",  name="user_seq_gen", sequenceName = "userSequence")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
