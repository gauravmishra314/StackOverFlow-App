package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }
    @PostMapping("/")
    public ResponseEntity<QuestionDTO> createQuestion(QuestionDTO questionDTO){
        QuestionDTO question = questionService.createQuestion(questionDTO);
        return ResponseEntity.ok(question);
    }
}
