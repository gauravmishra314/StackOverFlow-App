package com.springapp.stackoverflow.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapp.stackoverflow.dto.AnswerDTO;
import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.repository.AnswerRepository;
import com.springapp.stackoverflow.service.AnswerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.springapp.stackoverflow.service.CloudinaryService;

import java.io.IOException;

@RestController
@RequestMapping("/api/answers")
public class AnswerRestController {
    private CloudinaryService cloudinaryService;
    private final AnswerService answerService;

    public AnswerRestController(AnswerService answerService){
        this.answerService =answerService;
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnswerDTO> createAnswer(@RequestPart("answerDTO") AnswerDTO answerDTO,
                                                  @RequestPart(value = "file", required = false) MultipartFile file){
//        ObjectMapper objectMapper = new ObjectMapper();
//        //AnswerDTO answerDTO;
//        try {
//            //answerDTO = objectMapper.readValue(answerDTOString, AnswerDTO.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Invalid JSON format", e);
//        }
        String imageUrl = null;
        try {
            imageUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AnswerDTO responseAnser = answerService.createAnswer(answerDTO ,imageUrl,null);
        return ResponseEntity.ok(answerDTO);
    }
}
