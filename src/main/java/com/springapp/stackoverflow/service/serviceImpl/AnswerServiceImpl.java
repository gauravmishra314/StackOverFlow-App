package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.AnswerDTO;
import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.repository.AnswerRepository;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.service.AnswerService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final AnswerRepository answerRepository ;
    private ModelMapper modelMapper;
    private final QuestionRepository questionRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository,ModelMapper modelMapper
    ,QuestionRepository questionRepository){
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.questionRepository =questionRepository;
    }
    @Override
    public AnswerDTO createAnswer(AnswerDTO answerDTO, String imageUrl,Long id) {
        Answer answer = new Answer();
        // Ensure content is set correctly
        if (answerDTO.getBody() == null || answerDTO.getBody().trim().isEmpty()) {
            logger.warn("Question content is empty!");
            throw new RuntimeException("Content cannot be empty.");
        }
        Optional<Question> question = questionRepository.findById(id);
        answer.setBody(answerDTO.getBody());
        answer.setCreatedAt(LocalDateTime.now());
        answer.setImageUrl(answerDTO.getImageUrl());
        answer.setVoteCount(0);
        answer.setQuestion(question.get());
        answer.setUser(null);
        answerRepository.save(answer);
        //answer.setComments();
        return modelMapper.map(answer,AnswerDTO.class);

    }
}
