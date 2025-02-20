package com.springapp.stackoverflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentBlockDTO {
    private String type; // "text" or "image"
    private String text;
    private MultipartFile image;
}