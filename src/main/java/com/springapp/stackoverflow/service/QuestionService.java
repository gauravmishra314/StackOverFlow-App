package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;

public interface QuestionService {
    QuestionDTO createQuestion(QuestionDTO questionDTO);
}
