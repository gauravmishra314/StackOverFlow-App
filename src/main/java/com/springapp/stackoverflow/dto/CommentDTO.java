package com.springapp.stackoverflow.dto;

import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String body;
    private LocalDateTime createdAt;
    private User user;
    private Question question;
    private Answer answer;
}
