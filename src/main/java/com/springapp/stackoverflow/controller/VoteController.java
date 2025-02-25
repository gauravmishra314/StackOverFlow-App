package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.model.Vote;
import com.springapp.stackoverflow.service.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<Map<String, Integer>> voteQuestion(
            @PathVariable Long questionId,
            @RequestParam int voteType) {

        int totalVotes = voteService.voteQuestion(questionId, voteType);
        return ResponseEntity.ok(Map.of("totalVotes", totalVotes));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<Integer> getQuestionVotes(@PathVariable Long questionId) {
        int votes = voteService.getQuestionVotes(questionId);
        return ResponseEntity.ok(votes);
    }
}
