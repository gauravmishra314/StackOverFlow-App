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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final AnswerRepository answerRepository;
    private ModelMapper modelMapper;
    private final QuestionRepository questionRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository, ModelMapper modelMapper
            , QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.questionRepository = questionRepository;
    }

    @Override
    public AnswerDTO createAnswer(AnswerDTO answerDTO, String imageUrl, Long id) {
        if (answerDTO.getBody() == null || answerDTO.getBody().trim().isEmpty()) {
            logger.warn("Answer content is empty!");
            throw new RuntimeException("Content cannot be empty.");
        }

        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question not found.");
        }

        Answer answer = new Answer();
        answer.setBody(answerDTO.getBody());
        answer.setCreatedAt(LocalDateTime.now());
        answer.setImageUrl(imageUrl);
        answer.setVoteCount(0);
        answer.setQuestion(questionOpt.get());
        answerRepository.save(answer);

        return modelMapper.map(answer, AnswerDTO.class);
    }


    @Transactional(readOnly = true)
    @Override
    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtDesc(questionId);
        return answers.stream()
                .map(answer -> {
                    AnswerDTO dto = modelMapper.map(answer, AnswerDTO.class);
                    dto.setVoteCount(answer.getVoteCount());
                    if (answer.getUser() != null) {
                        dto.setUserName("karthik");
                        dto.setUserId(answer.getUser().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
