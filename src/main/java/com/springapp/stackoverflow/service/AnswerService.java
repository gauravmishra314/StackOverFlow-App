package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.dto.AnswerDTO;

public interface AnswerService {
    AnswerDTO createAnswer(AnswerDTO answerDTO,String imageUrl,Long id);
}
