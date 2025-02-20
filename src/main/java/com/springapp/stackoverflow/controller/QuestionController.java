package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.dto.UserDTO;
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
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    //private final UserService userService;
    private final TagService tagService;
    @Autowired
    public QuestionController(QuestionService questionService,TagService tagService){
        this.questionService = questionService;
        this.tagService = tagService;
    }

    @PostMapping("/")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO){
        QuestionDTO question = questionService.createQuestion(questionDTO);
        return ResponseEntity.ok(question);
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
    public ResponseEntity<QuestionDTO> viewQuestion(@PathVariable Long id){
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        return ResponseEntity.ok(questionDTO);
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

        UserDTO currentUser = null;

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<QuestionDTO> questionsPage = questionService.getAllQuestions(pageRequest);

        model.addAttribute("questions", questionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionsPage.getTotalPages());
        model.addAttribute("totalQuestions", questionService.getTotalQuestions());
        //model.addAttribute("tags",tagService.)

        return "home";
    }
}
