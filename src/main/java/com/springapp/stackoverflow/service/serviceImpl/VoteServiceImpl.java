package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.model.Vote;
import com.springapp.stackoverflow.repository.AnswerRepository;
import com.springapp.stackoverflow.repository.QuestionRepository;
import com.springapp.stackoverflow.repository.VoteRepository;
import com.springapp.stackoverflow.service.VoteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public VoteServiceImpl(VoteRepository voteRepository,
                           QuestionRepository questionRepository,
                           AnswerRepository answerRepository) {
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public int voteQuestion(Long questionId, int voteType, User currentUser) {
        if (voteType != 1 && voteType != -1) {
            throw new IllegalArgumentException("Vote type must be 1 or -1");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Check if user already voted on this question
        Vote existingVote = voteRepository.findByUserAndQuestion(currentUser, question);

        if (existingVote != null) {
            // If the vote is the same, remove it (cancel the vote)
            if (existingVote.getVote() == voteType) {
                voteRepository.delete(existingVote);
                // Update question's vote count
                updateQuestionVoteCount(question);
            } else {
                // Change the vote
                existingVote.setVote(voteType);
                voteRepository.save(existingVote);
                // Update question's vote count
                updateQuestionVoteCount(question);
            }
        } else {
            // Create new vote
            Vote vote = new Vote();
            vote.setQuestion(question);
            vote.setUser(currentUser);
            vote.setVote(voteType);
            vote.setCreatedAt(LocalDateTime.now());
            voteRepository.save(vote);
            // Update question's vote count
            updateQuestionVoteCount(question);
        }

        return getQuestionVotes(questionId);
    }

    @Transactional
    public int voteAnswer(Long answerId, int voteType, User currentUser) {
        if (voteType != 1 && voteType != -1) {
            throw new IllegalArgumentException("Vote type must be 1 or -1");
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Check if user already voted on this answer
        Vote existingVote = voteRepository.findByUserAndAnswer(currentUser, answer);

        if (existingVote != null) {
            // If the vote is the same, remove it (cancel the vote)
            if (existingVote.getVote() == voteType) {
                voteRepository.delete(existingVote);
                // Update answer's vote count
                updateAnswerVoteCount(answer);
            } else {
                // Change the vote
                existingVote.setVote(voteType);
                voteRepository.save(existingVote);
                // Update answer's vote count
                updateAnswerVoteCount(answer);
            }
        } else {
            // Create new vote
            Vote vote = new Vote();
            vote.setAnswer(answer);
            vote.setUser(currentUser);  // Fixed: set the current user instead of null
            vote.setVote(voteType);
            vote.setCreatedAt(LocalDateTime.now());
            voteRepository.save(vote);
            // Update answer's vote count
            updateAnswerVoteCount(answer);
        }

        return getAnswerVotes(answerId);
    }

    // Update question's vote count directly in the Question entity
    private void updateQuestionVoteCount(Question question) {
        int voteCount = voteRepository.sumVotesByQuestionId(question.getId());
        question.setVoteCount(voteCount);
        questionRepository.save(question);
    }

    // Update answer's vote count directly in the Answer entity
    private void updateAnswerVoteCount(Answer answer) {
        int voteCount = voteRepository.sumVotesByAnswerId(answer.getId());
        answer.setVoteCount(voteCount);
        answerRepository.save(answer);
    }

    public int getQuestionVotes(Long questionId) {
        return voteRepository.sumVotesByQuestionId(questionId);
    }

    public int getAnswerVotes(Long answerId) {
        return voteRepository.sumVotesByAnswerId(answerId);
    }

}
