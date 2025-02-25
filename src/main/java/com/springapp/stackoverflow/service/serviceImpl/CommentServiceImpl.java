package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.dto.CommentDTO;
import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Comment;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.repository.AnswerRepository;
import com.springapp.stackoverflow.repository.CommentRepository;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.repository.UserRepository;
import com.springapp.stackoverflow.service.CommentService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private Comment commentdtoToComment(CommentDTO commentDto) {
        return this.modelMapper.map(commentDto, Comment.class);
    }

    private CommentDTO commentTocommentDto(Comment comment) {
        return this.modelMapper.map(comment, CommentDTO.class);
    }

    @Transactional
    @Override
    public CommentDTO save(Long id, CommentDTO commentDto, String check) {
        Comment savedComment;
        if(check.equals("Question")){
            Optional<Question> questionOptional = questionRepository.findById(id);
            //Optional<User> userOptional = userRepository.findByEmail(principal.getName());
//        if(questionOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }
//        if(userOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }

            Comment comment = commentdtoToComment(commentDto);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setQuestion(questionOptional.get());
            //comment.setUser(userOptional.get());
            questionOptional.get().getComments().add(comment);
            savedComment = commentRepository.save(comment);
        }
        else {
            Optional<Answer> answerOptional = answerRepository.findById(id);
            //Optional<User> userOptional = userRepository.findByEmail(principal.getName());
//        if(questionOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }
//        if(userOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }

            Comment comment = commentdtoToComment(commentDto);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setAnswer(answerOptional.get());
            //comment.setUser(userOptional.get());
            answerOptional.get().getComments().add(comment);
            savedComment = commentRepository.save(comment);
        }


        return commentTocommentDto(savedComment);
    }

    @Transactional
    @Override
    public void delete(Long questionId, Long commentID, String typeReq) {
        if(typeReq.equals("Question")){
            Optional<Question> questionOptional = questionRepository.findById(questionId);
//        if(questionOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }

            List<Comment> commentAll = questionOptional.get().getComments();
            for(Comment comment : commentAll){
                if(comment.getId()==commentID){
                    commentAll.remove(comment) ;
                    commentRepository.deleteById(commentID);
                    questionOptional.get().setComments(commentAll);
                    break;
                }
            }
        }
        else{
            Optional<Answer> answerOptional = answerRepository.findById(questionId);
//        if(questionOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }

            List<Comment> commentAll = answerOptional.get().getComments();
            for(Comment comment : commentAll){
                if(comment.getId()==commentID){
                    commentAll.remove(comment) ;
                    commentRepository.deleteById(commentID);
                    answerOptional.get().setComments(commentAll);
                    break;
                }
            }
        }
    }

    @Transactional
    @Override
    public CommentDTO edit(Long id, Long commentID, CommentDTO commentDto) {
        Optional<Comment> commentOptional = commentRepository.findById(commentID);
//        if(commentOptional.isEmpty()){
//            throw new UsernameNotFoundException("Could not found user !!");
//        }
        Comment comment = commentOptional.get();
        comment.setBody(commentDto.getBody());
        comment.setCreatedAt(LocalDateTime.now());
        Comment editComment = commentRepository.save(comment);

        return commentTocommentDto(editComment);
    }
}
