package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.AnswerDTO;
import com.springapp.stackoverflow.dto.ContentBlockDTO;
import com.springapp.stackoverflow.service.AnswerService;
import com.springapp.stackoverflow.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.springapp.stackoverflow.service.CloudinaryService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);


    @Autowired
    public AnswerController(
            AnswerService answerService,
            TagService tagService,
            CloudinaryService cloudinaryService) {
        this.answerService = answerService;
        this.tagService = tagService;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/answers/{id}")
    public String createAnswer(@PathVariable Long id,
                               AnswerDTO answerDTO,
                               @RequestParam(value = "file", required = false) MultipartFile mainImage,
                               @RequestParam(value = "contentBlocks", required = false) List<ContentBlockDTO> contentBlocks,
                               @RequestParam(value = "content", required = false) String content,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        try {
            // Process main image
            String mainImageUrl = null;
            if (mainImage != null && !mainImage.isEmpty()) {
                try {
                    mainImageUrl = "https://res.cloudinary.com/dqmjfe5mg/image/upload/" + cloudinaryService.uploadImage(mainImage);
                    logger.info("Main image uploaded successfully: {}", mainImageUrl);
                } catch (IOException e) {
                    logger.error("Failed to upload main image", e);
                    redirectAttributes.addFlashAttribute("error", "Failed to upload main image");
                    return "redirect:/questions/ask";
                }
            }

            // Ensure content is set properly
            if (content == null || content.trim().isEmpty()) {
                logger.warn("No content received from form submission.");
                redirectAttributes.addFlashAttribute("error", "Content cannot be empty.");
                return "redirect:/questions/ask";
            }

            answerDTO.setImageUrl(mainImageUrl);
            answerDTO.setBody(content);

            //answerDTO savedAnswer = answerService.createAnswer(questionDTO, mainImageUrl, new ArrayList<>());
             answerService.createAnswer(answerDTO,null,id);
            return "redirect:/questions/" + id;

        } catch (Exception e) {
            logger.error("Error creating question", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create question: " + e.getMessage());
            return "redirect:/questions/"+id;
        }
    }
}
