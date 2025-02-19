package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;

import com.springapp.stackoverflow.dto.QuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
    Page<QuestionDTO> getAllQuestions(Pageable pageable);
    void deleteQuestion(Long id);
    QuestionDTO createQuestion(QuestionDTO questionDTO);
}
