package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.Tag;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.repository.TagRepository;
import com.springapp.stackoverflow.service.QuestionService;
import com.springapp.stackoverflow.service.TagService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private ModelMapper modelMapper;
    private TagRepository tagRepository;
    private TagService tagService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ModelMapper modelMapper,
                               TagRepository tagRepository,
                               TagService tagService) {
        this.questionRepository = questionRepository;
        this.modelMapper=modelMapper;
        this.tagRepository = tagRepository;
        this.tagService=tagService;
    }

    private Question questiondtoToQuestion(QuestionDTO questionDTO) {
        return this.modelMapper.map(questionDTO, Question.class);
    }

    private QuestionDTO questionToquestionDto(Question question) {
        return this.modelMapper.map(question, QuestionDTO.class);
    }

    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO, String imageUrl) {
        return createQuestion(questionDTO, imageUrl, new ArrayList<>());
    }

    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO, String mainImageUrl, List<String> contentImageUrls) {
        Question question = new Question();
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setExcerpt(questionDTO.getExcerpt());

        // Explicitly set the image URL
        question.setImageURL(mainImageUrl);
        logger.info("Setting main image URL: {}", mainImageUrl);

        if (questionDTO.getUser() != null) {
            question.setUser(modelMapper.map(questionDTO.getUser(), User.class));
        }

        LocalDateTime now = LocalDateTime.now();
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        // Set content images if provided
        if (contentImageUrls != null && !contentImageUrls.isEmpty()) {
            question.setContentImages(contentImageUrls);
            logger.info("Setting {} content images", contentImageUrls.size());
        }

        question.setVoteCount(0);
        question.setAnswerCount(0);
        question.setViewsCount(0);

        // Process tags
        List<Tag> tags = new ArrayList<>();
        if (questionDTO.getTags() != null) {
            for (String tagName : questionDTO.getTags()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = tagService.findOrCreateTag(tagName);
                    tags.add(tag);
                }
            }
        }
        question.setTags(tags);

        Question savedQuestion = questionRepository.save(question);
        logger.info("Question saved with ID: {} and image URL: {}", savedQuestion.getId(), savedQuestion.getImageURL());

        return convertToDTO(savedQuestion);
    }
    @Override
    public QuestionDTO getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        QuestionDTO questionDTO = modelMapper.map(question,QuestionDTO.class);
        return questionDTO;
    }

    @Override
    public QuestionDTO edit(Long questionID, QuestionDTO questionDTO) {
        Optional<Question> questionOptional = questionRepository.findById(questionID);
        Question editedQuestion = questionOptional.get();
        editedQuestion.setTitle(questionDTO.getTitle());
        editedQuestion.setContent(questionDTO.getContent());
        editedQuestion.setUpdatedAt(questionDTO.getUpdatedAt());
        editedQuestion.setExcerpt(questionDTO.getExcerpt());

        List<Tag> tags = new ArrayList<>();
        if (questionDTO.getTags() != null) {
            for (String tagName : questionDTO.getTags()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = tagService.findOrCreateTag(tagName);
                    tags.add(tag);
                }
            }
        }
        editedQuestion.setViewsCount(questionDTO.getViewsCount());
        editedQuestion.setTags(tags);
        questionRepository.save(editedQuestion);
        return questionToquestionDto(editedQuestion);
    }

    @Override
    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(this::convertToDTO);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        questionRepository.delete(question);
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = modelMapper.map(question, QuestionDTO.class);

        List<String> tagNames = new ArrayList<>();
        if (question.getTags() != null) {
            for (var tag : question.getTags()) {
                tagNames.add(tag.getName());
            }
        }
        dto.setTags(tagNames);

        return dto;
    }

    @Override
    public long getTotalQuestions() {
        return questionRepository.count();
    }

    @Override
    public List<Tag> findTagsByQuestionId(Long id) {
        List<Tag> tagList = questionRepository.findTagsByQuestionId(id);
        return tagList;
    }
}
