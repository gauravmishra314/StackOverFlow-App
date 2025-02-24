package com.springapp.stackoverflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private Long id;
    private Long userId;
    private Long questionId;
    private String body;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String userName;
    private int voteCount;
}