package com.springapp.stackoverflow.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private int voteCount;
    private int answerCount;
    private int viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO user;
    private List<String> tags;
}
