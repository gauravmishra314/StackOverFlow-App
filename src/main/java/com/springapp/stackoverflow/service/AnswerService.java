package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.AnswerDTO;

import java.util.List;

public interface AnswerService {
    AnswerDTO createAnswer(AnswerDTO answerDTO,String imageUrl,Long id);
    List<AnswerDTO> getAnswersByQuestionId(Long questionId);
}
