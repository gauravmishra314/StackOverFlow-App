package com.springapp.stackoverflow.rest;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.service.CloudinaryService;
import com.springapp.stackoverflow.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/questions")
public class QuestionRestController {
    private final QuestionService questionService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    public QuestionRestController(QuestionService questionService){
        this.questionService = questionService;
    }

    @PostMapping("/")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO,@RequestParam("file") MultipartFile file){
        String imageUrl = null;
        try {
            imageUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        QuestionDTO question = questionService.createQuestion(questionDTO,imageUrl);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<QuestionDTO>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<QuestionDTO> questions = questionService.getAllQuestions(pageRequest);
        return ResponseEntity.ok(questions);
    }
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> viewQuestion(@PathVariable Long id){
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        return ResponseEntity.ok(questionDTO);
    }

    @PutMapping("/{questionID}")
    public ResponseEntity<QuestionDTO> editQuestion(@PathVariable Long questionID, @RequestBody QuestionDTO questionDTO){
        QuestionDTO questionDTOEdited = questionService.edit(questionID,questionDTO);
        return new ResponseEntity<>(questionDTOEdited, HttpStatus.OK);
    }
}
