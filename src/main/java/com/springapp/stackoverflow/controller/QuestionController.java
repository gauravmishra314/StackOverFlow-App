package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.QuestionDTO;
import com.springapp.stackoverflow.service.QuestionService;
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

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    @Autowired
    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }

    @GetMapping("/ask")
    public String showAskQuestionForm(Model model) {
        model.addAttribute("questionDTO", new QuestionDTO());
        return "ask-question-page";
    }

    @PostMapping("/ask")
    public String submitQuestion(@ModelAttribute("questionDTO") QuestionDTO questionDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "ask-question-page";
        }

        QuestionDTO savedQuestion = questionService.createQuestion(questionDTO);
        return "redirect:/questions/" + savedQuestion.getId();
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
}
