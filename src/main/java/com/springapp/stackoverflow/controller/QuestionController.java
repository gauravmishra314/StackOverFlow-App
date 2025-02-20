package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.ContentBlockDTO;
import com.springapp.stackoverflow.dto.QuestionDTO;
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
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);


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
            @RequestParam(value = "file", required = false) MultipartFile mainImage,
            @RequestParam(value = "contentBlocks", required = false) List<ContentBlockDTO> contentBlocks,
            @RequestParam(value = "contentBlocks[].image", required = false) MultipartFile[] contentImages,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        try {
            // Process main image first
            String mainImageUrl = null;
            if (mainImage != null && !mainImage.isEmpty()) {
                try {
                    System.out.println("Image from the view ----"+mainImage);
                    mainImageUrl = cloudinaryService.uploadImage(mainImage);
                    logger.info("Main image uploaded successfully: {}", mainImageUrl);
                } catch (IOException e) {
                    logger.error("Failed to upload main image", e);
                    redirectAttributes.addFlashAttribute("error", "Failed to upload main image");
                    return "redirect:/questions/ask";
                }
            }

            // Process content and content images
            StringBuilder contentBuilder = new StringBuilder();
            List<String> contentImageUrls = new ArrayList<>();

            if (contentBlocks != null && contentImages != null) {
                for (int i = 0; i < contentBlocks.size(); i++) {
                    ContentBlockDTO block = contentBlocks.get(i);
                    if ("text".equals(block.getType())) {
                        contentBuilder.append(block.getText()).append("\n\n");
                    } else if ("image".equals(block.getType()) && i < contentImages.length) {
                        MultipartFile image = contentImages[i];
                        if (image != null && !image.isEmpty()) {
                            try {
                                String imageUrl = cloudinaryService.uploadImage(image);
                                contentImageUrls.add(imageUrl);
                                contentBuilder.append("![Image ").append(i + 1).append("](").append(imageUrl).append(")\n\n");
                                logger.info("Content image uploaded successfully: {}", imageUrl);
                            } catch (IOException e) {
                                logger.error("Failed to upload content image", e);
                            }
                        }
                    }
                }
            }

            questionDTO.setContent(contentBuilder.toString());
            questionDTO.setImageURL(mainImageUrl);

            QuestionDTO savedQuestion = questionService.createQuestion(questionDTO, mainImageUrl, contentImageUrls);
            return "redirect:/questions/" + savedQuestion.getId();

        } catch (Exception e) {
            logger.error("Error creating question", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create question: " + e.getMessage());
            return "redirect:/questions/ask";
        }
    }

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

        return "questions-page"; // This will look for questions.html in your templates folder
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        model.addAttribute("question", questionDTO);
        return "question-details";
    }

    @PutMapping("/{questionID}")
    public ResponseEntity<QuestionDTO> editQuestion(@PathVariable Long questionID, @RequestBody QuestionDTO questionDTO){
        QuestionDTO questionDTOEdited = questionService.edit(questionID,questionDTO);
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