package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.QuestionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {
    Page<QuestionDTO> getAllQuestions(Pageable pageable);
    void deleteQuestion(Long id);
    QuestionDTO createQuestion(QuestionDTO questionDTO, String imageUrl);
    QuestionDTO getQuestionById(Long id);
    QuestionDTO edit(Long questionID, QuestionDTO questionDTO);
    long getTotalQuestions();
}
