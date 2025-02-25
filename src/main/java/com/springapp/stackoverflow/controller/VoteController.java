package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<?> voteQuestion(
            @PathVariable Long questionId,
            @RequestParam int voteType) {
        try {
            int totalVotes = voteService.voteQuestion(questionId, voteType,null);
            return ResponseEntity.ok(Map.of(
                    "totalVotes", totalVotes,
                    "success", true,
                    "message", "Vote recorded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/answer/{answerId}")
    public ResponseEntity<?> voteAnswer(
            @PathVariable Long answerId,
            @RequestParam int voteType) {
        try {
            int totalVotes = voteService.voteAnswer(answerId, voteType,null);
            return ResponseEntity.ok(Map.of(
                    "totalVotes", totalVotes,
                    "success", true,
                    "message", "Vote recorded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<Integer> getQuestionVotes(@PathVariable Long questionId) {
        int votes = voteService.getQuestionVotes(questionId);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/answer/{answerId}")
    public ResponseEntity<Integer> getAnswerVotes(@PathVariable Long answerId) {
        int votes = voteService.getAnswerVotes(answerId);
        return ResponseEntity.ok(votes);
    }
}
