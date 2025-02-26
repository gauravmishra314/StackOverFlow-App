package com.springapp.stackoverflow.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    private String profilePic;
    private String bio;

    @Column(name="role")
    private String role;

    @ElementCollection
    private List<String> interestedTopics;

    @ManyToMany
    @JoinTable(
            name = "user_saved_questions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> savedQuestions;

    private String badges;
    private int reputations;

    private LocalDateTime createdAt;  // Will be set manually in the service layer
}
