package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.Tag;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.repository.TagRepository;
import com.springapp.stackoverflow.service.QuestionService;
import com.springapp.stackoverflow.service.TagService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // Ensure content is set correctly
        if (questionDTO.getContent() == null || questionDTO.getContent().trim().isEmpty()) {
            logger.warn("Question content is empty!");
            throw new RuntimeException("Content cannot be empty.");
        }

        question.setContent(questionDTO.getContent());
        question.setExcerpt(questionDTO.getExcerpt());
        question.setImageURL(mainImageUrl);

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
        logger.info("Question saved with ID: {} and content: {}", savedQuestion.getId(), savedQuestion.getContent());

        return convertToDTO(savedQuestion);
    }

    @Override
    public QuestionDTO getQuestionById(Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            QuestionDTO dto = modelMapper.map(question, QuestionDTO.class);

            // Convert tags to string list
            if (question.getTags() != null) {
                List<String> tagNames = question.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList());
                dto.setTags(tagNames);
            }

            return dto;
        }
        throw new RuntimeException("Question not found with id: " + id);
    }

    @Override
    public QuestionDTO edit(Long questionId, QuestionDTO questionDTO) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        // Update basic fields
        existingQuestion.setTitle(questionDTO.getTitle());
        existingQuestion.setContent(questionDTO.getContent());
        existingQuestion.setExcerpt(questionDTO.getExcerpt());
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        // Update tags
        List<Tag> tags = new ArrayList<>();
        if (questionDTO.getTags() != null) {
            for (String tagName : questionDTO.getTags()) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = tagService.findOrCreateTag(tagName);
                    tags.add(tag);
                }
            }
        }
        existingQuestion.setTags(tags);

        // Save the updated question
        Question updatedQuestion = questionRepository.save(existingQuestion);
        return convertToDTO(updatedQuestion);
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

    @Override
    public Page<QuestionDTO> searchQuestions(String query, String tags, Pageable pageable) {
        Specification<Question> spec = Specification.where(null);

        if (query != null && !query.trim().isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + query.toLowerCase() + "%"
                    )
            );
        }

        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty()) {
                    spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                        Join<Question, Tag> tagJoin = root.join("tags", JoinType.INNER);
                        return criteriaBuilder.equal(
                                criteriaBuilder.lower(tagJoin.get("name")),
                                trimmedTag.toLowerCase()
                        );
                    });
                }
            }
        }

        Page<Question> questions = questionRepository.findAll(spec, pageable);

        return questions.map(this::convertToDTO);
    }

    @Transactional
    @Override
    public Page<QuestionDTO> unAnsweredQuestion(String query, String tags, Pageable pageable) {
        Page<Question> questions = questionRepository.findUnansweredQuestions(pageable);

        List<QuestionDTO> filteredQuestions = questions.getContent().stream()
                .filter(q -> q.getAnswers().isEmpty()) // Keep only unanswered questions
                .map(this::convertToDTO) // Convert to DTO
                .collect(Collectors.toList());

        return new PageImpl<>(filteredQuestions, pageable, filteredQuestions.size());
    }
}
