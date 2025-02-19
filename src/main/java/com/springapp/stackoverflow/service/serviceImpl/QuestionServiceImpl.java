package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.Tag;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.repository.TagRepository;
import com.springapp.stackoverflow.service.QuestionService;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private ModelMapper modelMapper;
    private TagRepository tagRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ModelMapper modelMapper,
                               TagRepository tagRepository){
        this.questionRepository = questionRepository;
        this.modelMapper=modelMapper;
        this.tagRepository = tagRepository;
    }
    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question question = modelMapper.map(questionDTO , Question.class);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        List<Tag> tags = new ArrayList<>();
        String[] tagList = questionDTO.getTagList().split(",");
        for(String tag:tagList){
            Tag currTag= new Tag();
            currTag.setName(tag);
            tagRepository.save(currTag);
            tags.add(currTag);
        }
        question.setTags(tags);
        questionRepository.save(question);
        QuestionDTO questionDto = modelMapper.map(question,QuestionDTO.class);
        return questionDto;
    }
}
