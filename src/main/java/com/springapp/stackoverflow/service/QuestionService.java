package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.QuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    QuestionDTO createQuestion(QuestionDTO questionDTO, String imageUrl);

    // New method to support multiple content images
    QuestionDTO createQuestion(QuestionDTO questionDTO, String mainImageUrl, List<String> contentImageUrls);

    QuestionDTO getQuestionById(Long id);

    QuestionDTO edit(Long questionID, QuestionDTO questionDTO);

    Page<QuestionDTO> getAllQuestions(Pageable pageable);

    void deleteQuestion(Long id);

    long getTotalQuestions();
}