package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.AnswerDTO;
import com.springapp.stackoverflow.service.AnswerService;
import com.springapp.stackoverflow.service.CloudinaryService;
import com.springapp.stackoverflow.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

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
    public String createAnswer(
            @PathVariable Long id,
            AnswerDTO answerDTO,
            @RequestParam(value = "contentBlocksData", required = false) String[] contentBlocksData,
            @RequestParam(value = "contentBlockTypes", required = false) String contentBlockTypes,
            @RequestParam(value = "contentImages", required = false) MultipartFile[] contentImages,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "question-details";
        }

        try {
            StringBuilder contentBuilder = new StringBuilder();
            List<String> contentImageUrls = new ArrayList<>();
            int imageCounter = 0;

            // Parse the content block types
            String[] blockTypes = contentBlockTypes != null ? contentBlockTypes.split(",") : new String[0];

            // First check if we have a single text entry with no block types
            if ((blockTypes.length == 0 || blockTypes.length == 1 && "text".equals(blockTypes[0]))
                    && contentBlocksData != null && contentBlocksData.length == 1) {
                // Handle the single text block case
                String text = contentBlocksData[0];
                if (text != null && !text.trim().isEmpty()) {
                    contentBuilder.append(text);
                }
            }
            // Otherwise process multiple blocks
            else if (blockTypes.length > 0) {
                for (int i = 0; i < blockTypes.length; i++) {
                    String type = blockTypes[i];

                    if ("text".equals(type) && contentBlocksData != null && i < contentBlocksData.length) {
                        String text = contentBlocksData[i];
                        if (text != null && !text.trim().isEmpty()) {
                            // Wrap text in paragraph tags if needed for Markdown
                            if (!text.startsWith("#") && !text.startsWith("*") && !text.startsWith("-")) {
                                contentBuilder.append(text).append("\n\n");
                            } else {
                                contentBuilder.append(text).append("\n\n");
                            }
                        }
                    } else if ("image".equals(type) && contentImages != null && imageCounter < contentImages.length) {
                        MultipartFile image = contentImages[imageCounter];
                        if (image != null && !image.isEmpty()) {
                            try {
                                // Upload image to Cloudinary
                                String imageUrl = cloudinaryService.uploadImage(image);
                                contentImageUrls.add(imageUrl);
                                // Add image markdown to content
                                contentBuilder.append("![Image ")
                                        .append(imageCounter + 1)
                                        .append("](")
                                        .append(imageUrl)
                                        .append(")\n\n");
                                imageCounter++;
                            } catch (IOException e) {
                                logger.error("Failed to upload answer image", e);
                                throw new RuntimeException("Failed to upload image", e);
                            }
                        }
                    }
                }
            }

            // Set the content to the AnswerDTO
            String finalContent = contentBuilder.toString().trim();
            logger.info("Final answer content: {}", finalContent);
            answerDTO.setBody(finalContent);

            answerService.createAnswer(answerDTO, null, id);
            return "redirect:/questions/" + id;

        } catch (Exception e) {
            logger.error("Error creating answer", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create answer: " + e.getMessage());
            return "redirect:/questions/" + id;
        }
    }
}