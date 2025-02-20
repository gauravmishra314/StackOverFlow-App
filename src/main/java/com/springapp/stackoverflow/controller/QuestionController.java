package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.ContentBlockDTO;
import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.dto.UserDTO;
import com.springapp.stackoverflow.service.CloudinaryService;
import com.springapp.stackoverflow.service.QuestionService;
import com.springapp.stackoverflow.service.TagService;
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

import java.io.IOException;
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

    @Autowired
    public QuestionController(
            QuestionService questionService,
            TagService tagService,
            CloudinaryService cloudinaryService) {
        this.questionService = questionService;
        this.tagService = tagService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/ask")
    public String showAskQuestionForm(Model model) {
        model.addAttribute("questionDTO", new QuestionDTO());
        return "ask-question-page";
    }

    @PostMapping("/ask")
    public String submitQuestion(
            @ModelAttribute("questionDTO") QuestionDTO questionDTO,
            @RequestParam(value = "contentBlocks", required = false) List<ContentBlockDTO> contentBlocks,
            @RequestParam(value = "file", required = false) MultipartFile mainImage,
            @RequestParam(value = "contentBlocks[0].image", required = false) MultipartFile[] contentImages,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        // Process tags if they came as a comma-separated string
        if (questionDTO.getTags() != null && questionDTO.getTags().size() == 1 && questionDTO.getTags().get(0).contains(",")) {
            String tagString = questionDTO.getTags().get(0);
            List<String> tagList = Arrays.stream(tagString.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            questionDTO.setTags(tagList);
        }

        // Upload main image if provided
        String mainImageUrl = null;
        try {
            if (mainImage != null && !mainImage.isEmpty()) {
                mainImageUrl = cloudinaryService.uploadImage(mainImage);
            }

            // Handle content blocks with images
            List<String> contentImageUrls = new ArrayList<>();
            if (contentImages != null) {
                for (MultipartFile imgFile : contentImages) {
                    if (imgFile != null && !imgFile.isEmpty()) {
                        String imageUrl = cloudinaryService.uploadImage(imgFile);
                        contentImageUrls.add(imageUrl);

                        // Replace image placeholders in content with actual image URLs
                        if (questionDTO.getContent() != null) {
                            for (int i = 0; i < contentImageUrls.size(); i++) {
                                String placeholder = "[IMAGE_PLACEHOLDER_" + i + "]";
                                String imageHtml = "![Image " + (i+1) + "](" + contentImageUrls.get(i) + ")";
                                questionDTO.setContent(questionDTO.getContent().replace(placeholder, imageHtml));
                            }
                        }
                    }
                }
            }

            QuestionDTO savedQuestion = questionService.createQuestion(questionDTO, mainImageUrl, contentImageUrls);
            return "redirect:/questions/" + savedQuestion.getId();

        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image. Please try again.");
            return "ask-question-page";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "ask-question-page";
        }
    }

    // Other methods remain unchanged
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
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

        return "questions-page";
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        model.addAttribute("question", questionDTO);
        return "question-details";
    }

    @PutMapping("/{questionID}")
    public ResponseEntity<QuestionDTO> editQuestion(@PathVariable Long questionID, @RequestBody QuestionDTO questionDTO) {
        QuestionDTO questionDTOEdited = questionService.edit(questionID, questionDTO);
        return new ResponseEntity<>(questionDTOEdited, HttpStatus.OK);
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