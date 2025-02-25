package com.springapp.stackoverflow.controller;

import com.springapp.stackoverflow.dto.CommentDTO;
import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Comment;
import com.springapp.stackoverflow.repository.AnswerRepository;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.service.CommentService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping("/questions/{id}/comment")
    public String addCommentQuestion(@PathVariable Long id, @RequestBody CommentDTO commentDto, Model model){
        Long targetID = id;
        CommentDTO savedCommentDto = commentService.save(targetID,commentDto,"Question");
        List<Comment> comments = savedCommentDto.getQuestion().getComments();
        Collections.reverse(comments);
        model.addAttribute("comments",comments);
        return "question-details";
    }

    @PostMapping("/answer/{id}/comment")
    public String addCommentAnswer(@PathVariable Long id, @RequestBody CommentDTO commentDto, Model model){
        Long targetID = id;
        CommentDTO savedCommentDto = commentService.save(targetID,commentDto, "Answer");
        List<Comment> answerComment = savedCommentDto.getAnswer().getComments();
        Collections.reverse(answerComment);
        model.addAttribute("comments",answerComment);
        return "question-details";
    }

    @PostMapping("/questions/{id}/comment/{deleteID}")
    public String deleteCommentQuestion(@PathVariable Long id,@PathVariable Long deleteID){
        String typeReq = "Question";
        commentService.delete(id,deleteID,typeReq);
        return "question-details";
    }

    @PostMapping("/answer/{id}/comment/deleteID")
    public String deleteCommentAnswer(@PathVariable Long deleteID, @PathVariable Long id){
        Long targetID = id;
        String typeReq = "Answer";
        commentService.delete(targetID,deleteID,typeReq);
        return "question-details";
    }

    @PostMapping("/questions/{questionId}/edit/{commentId}")
    public String editCommentQuestion(
            @PathVariable Long questionId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDto) {

        CommentDTO editCommentDto = commentService.edit(questionId, commentId, commentDto);
        return "question-details";
    }

    @PostMapping("/answer/{answerId}/comment/{commentId}")
    public String editCommentAnswer(
            @PathVariable Long answerId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDto) {

        CommentDTO editCommentDto = commentService.edit(answerId, commentId, commentDto);
        return "question-details";
    }
}

//@RestController
//public class CommentController {
//    @Autowired
//    private CommentService commentService;
//
//    @Autowired
//    private QuestionRepository questionRepository;
//
//    @Autowired
//    private AnswerRepository answerRepository;
//
////    @PostMapping(value = {"/questions/{id}/comment", "/answer/{answerId}/comment"})
////    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id, @PathVariable Long answerId, @RequestBody CommentDTO commentDto,
////                                                 Principal principal){
////        Long targetID = id==null?answerId:id;
////
////        CommentDTO savedCommentDto = commentService.save(targetID,commentDto, principal);
////        return new ResponseEntity<>(savedCommentDto, HttpStatus.CREATED);
////    }
//
//    @Transactional
//    @PostMapping("/questions/{id}/comment")
//    public ResponseEntity<CommentDTO> addCommentQuestion(@PathVariable Long id, @RequestBody CommentDTO commentDto){
//        Long targetID = id;
//        CommentDTO savedCommentDto = commentService.save(targetID,commentDto,"Question");
//        List<Comment> comments = savedCommentDto.getQuestion().getComments();
//        Collections.reverse(comments);
//
//        return new ResponseEntity<>(savedCommentDto, HttpStatus.CREATED);
//    }
//
//    @Transactional
//    @PostMapping("/answer/{id}/comment")
//    public ResponseEntity<CommentDTO> addCommentAnswer(@PathVariable Long id, @RequestBody CommentDTO commentDto){
//        Long targetID = id;
//        CommentDTO savedCommentDto = commentService.save(targetID,commentDto, "Answer");
//        Optional<Answer> q = answerRepository.findById(targetID);
//        if (q.isPresent()) {
//            Answer question = q.get();
//            Hibernate.initialize(question.getComments());  // Force Hibernate to load comments
//            List<Comment> comments = question.getComments();
//            Collections.reverse(comments);
//            for (Comment comment : comments) {
//                System.out.println(comment.getBody());
//            }
//        }
//
//        return new ResponseEntity<>(savedCommentDto, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/questions/{id}/comment/{deleteID}")
//    public ResponseEntity<String> deleteCommentQuestion(@PathVariable Long id,@PathVariable Long deleteID){
//
//        String typeReq = "Question";
//        commentService.delete(id,deleteID,typeReq);
//        return new ResponseEntity<>("Comment Deleted Successfully", HttpStatus.OK);
//    }
//
//    @DeleteMapping("/answer/{id}/comment/deleteID")
//    public ResponseEntity<String> deleteCommentAnswer(@PathVariable Long deleteID, @PathVariable Long id){
//        Long targetID = id;
//        String typeReq = "Answer";
//        commentService.delete(targetID,deleteID,typeReq);
//        return new ResponseEntity<>("Comment Deleted Successfully", HttpStatus.OK);
//    }
//
//    @PutMapping("/questions/{questionId}/edit/{commentId}")
//    public ResponseEntity<CommentDTO> editCommentQuestion(
//            @PathVariable Long questionId,
//            @PathVariable Long commentId,
//            @RequestBody CommentDTO commentDto) {
//
//        CommentDTO editCommentDto = commentService.edit(questionId, commentId, commentDto, "Question");
//        return new ResponseEntity<>(editCommentDto, HttpStatus.OK);
//    }
//
//    @PutMapping("/answer/{answerId}/comment/{commentId}")
//    public ResponseEntity<CommentDTO> editCommentAnswer(
//            @PathVariable Long answerId,
//            @PathVariable Long commentId,
//            @RequestBody CommentDTO commentDto) {
//
//        CommentDTO editCommentDto = commentService.edit(answerId, commentId, commentDto, "Answer");
//        return new ResponseEntity<>(editCommentDto, HttpStatus.OK);
//    }
//}