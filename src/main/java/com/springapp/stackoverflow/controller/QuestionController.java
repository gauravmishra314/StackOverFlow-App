package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.AnswerDTO;
import com.springapp.stackoverflow.dto.ContentBlockDTO;
import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Tag;
import com.springapp.stackoverflow.service.AnswerService;
import com.springapp.stackoverflow.service.CloudinaryService;
import com.springapp.stackoverflow.service.QuestionService;
import com.springapp.stackoverflow.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private final AnswerService answerService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);


    @Autowired
    public QuestionController(
            QuestionService questionService,
            TagService tagService,
            CloudinaryService cloudinaryService,AnswerService answerService) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.cloudinaryService = cloudinaryService;
        this.answerService = answerService;
    }

    @GetMapping("/ask")
    public String showAskQuestionForm(Model model) {
        model.addAttribute("questionDTO", new QuestionDTO());
        return "ask-question-page";
    }

    @PostMapping("/ask")
    public String submitQuestion(
            @ModelAttribute("questionDTO") QuestionDTO questionDTO,
            @RequestParam(value = "contentBlocksData", required = false) String[] contentBlocksData,
            @RequestParam(value = "contentBlockTypes", required = false) String[] contentBlockTypes,
            @RequestParam(value = "contentImages", required = false) MultipartFile[] contentImages,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        try {
            StringBuilder contentBuilder = new StringBuilder();
            List<String> contentImageUrls = new ArrayList<>();
            int imageCounter = 0;

            if (contentBlockTypes != null) {
                for (int i = 0; i < contentBlockTypes.length; i++) {
                    String type = contentBlockTypes[i];

                    if ("text".equals(type) && i < contentBlocksData.length) {
                        String text = contentBlocksData[i];
                        if (text != null && !text.trim().isEmpty()) {
                            contentBuilder.append(text).append("\n\n");
                        }
                    } else if ("image".equals(type) && contentImages != null && imageCounter < contentImages.length) {
                        MultipartFile image = contentImages[imageCounter];
                        if (image != null && !image.isEmpty()) {
                            try {
                                // Get the full secure URL from CloudinaryService
                                String imageUrl = cloudinaryService.uploadImage(image);
                                contentImageUrls.add(imageUrl);
                                // Use the full URL in the markdown
                                contentBuilder.append("![Image ")
                                        .append(imageCounter + 1)
                                        .append("](")
                                        .append(imageUrl)
                                        .append(")\n\n");
                                imageCounter++;
                            } catch (IOException e) {
                                logger.error("Failed to upload content image", e);
                                throw new RuntimeException("Failed to upload image", e);
                            }
                        }
                    }
                }
            }

            questionDTO.setContent(contentBuilder.toString());
            //QuestionDTO savedQuestion = questionService.createQuestion(questionDTO, null, contentImageUrls);
            QuestionDTO savedQuestion = questionService.createQuestion(questionDTO, contentBuilder.toString(), contentImageUrls);

            return "redirect:/questions/" + savedQuestion.getId();

        } catch (Exception e) {
            logger.error("Error creating question", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create question: " + e.getMessage());
            return "redirect:/questions/ask";
        }
    }


    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/questions";
    }

    @GetMapping
    public String getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            Model model) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<QuestionDTO> questions = questionService.getAllQuestions(pageRequest);

        model.addAttribute("questions", questions.getContent());
        model.addAttribute("totalQuestions", questionService.getTotalQuestions());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questions.getTotalPages());

        return "questions-page"; // This will look for questions.html in your templates folder
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        List<Tag> tags = questionService.findTagsByQuestionId(id);
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(id);

        System.out.println("Question content: " + questionDTO.getContent());
        model.addAttribute("question", questionDTO);
        model.addAttribute("tag", tags);
        model.addAttribute("answers", answers);
        return "question-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditQuestionForm(@PathVariable Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        List<String> tagNames = questionDTO.getTags() != null ?
                questionDTO.getTags() :
                new ArrayList<>();
        questionDTO.setTags(tagNames);

        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("isEditMode", true);
        return "ask-question-page";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(
            @PathVariable Long id,
            @ModelAttribute("questionDTO") QuestionDTO questionDTO,
            @RequestParam(value = "contentBlocksData", required = false) String[] contentBlocksData,
            @RequestParam(value = "contentBlockTypes", required = false) String[] contentBlockTypes,
            @RequestParam(value = "contentImages", required = false) MultipartFile[] contentImages,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        try {
            StringBuilder contentBuilder = new StringBuilder();
            List<String> contentImageUrls = new ArrayList<>();
            int imageCounter = 0;

            if (contentBlockTypes != null) {
                for (int i = 0; i < contentBlockTypes.length; i++) {
                    String type = contentBlockTypes[i];

                    if ("text".equals(type) && i < contentBlocksData.length) {
                        String text = contentBlocksData[i];
                        if (text != null && !text.trim().isEmpty()) {
                            contentBuilder.append(text).append("\n\n");
                        }
                    } else if ("image".equals(type) && contentImages != null && imageCounter < contentImages.length) {
                        MultipartFile image = contentImages[imageCounter];
                        if (image != null && !image.isEmpty()) {
                            try {
                                String imageUrl = cloudinaryService.uploadImage(image);
                                contentImageUrls.add(imageUrl);
                                contentBuilder.append("![Image ")
                                        .append(imageCounter + 1)
                                        .append("](")
                                        .append(imageUrl)
                                        .append(")\n\n");
                                imageCounter++;
                            } catch (IOException e) {
                                logger.error("Failed to upload content image", e);
                                throw new RuntimeException("Failed to upload image", e);
                            }
                        }
                    }
                }
            }

            // Set the content
            questionDTO.setContent(contentBuilder.toString());
            questionDTO.setId(id);
            questionDTO.setUpdatedAt(LocalDateTime.now());

            // Update the question
            QuestionDTO updatedQuestion = questionService.edit(id, questionDTO);
            redirectAttributes.addFlashAttribute("message", "Question updated successfully");
            return "redirect:/questions/" + updatedQuestion.getId();

        } catch (Exception e) {
            logger.error("Error updating question", e);
            redirectAttributes.addFlashAttribute("error", "Failed to update question: " + e.getMessage());
            return "redirect:/questions/edit/" + id;
        }
    }

    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            Model model) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<QuestionDTO> questionsPage = questionService.getAllQuestions(pageRequest);

        model.addAttribute("questions", questionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionsPage.getTotalPages());
        model.addAttribute("totalQuestions", questionService.getTotalQuestions());

        return "home";
    }
}