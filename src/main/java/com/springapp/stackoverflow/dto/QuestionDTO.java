package com.springapp.stackoverflow.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private int voteCount;
    private String tagList;
    private int answerCount;
    private int viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
