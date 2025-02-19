package com.springapp.stackoverflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private int voteCount;
    private int answerCount;
    private int viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageURL;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Tag> tags;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmarked> bookmarks;
}
