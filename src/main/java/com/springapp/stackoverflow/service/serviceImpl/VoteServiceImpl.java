package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.Vote;
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

    @Autowired
    public VoteServiceImpl(VoteRepository voteRepository, QuestionRepository questionRepository) {
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public int voteQuestion(Long questionId, int voteType) {
        if (voteType != 1 && voteType != -1) {
            throw new IllegalArgumentException("Vote type must be 1 or -1");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Create new vote
//        Vote vote = Vote.builder()
//                .question(question)
//                .vote(voteType)
//                .createdAt(LocalDateTime.now())
//                .build();
        Vote vote = new Vote();
        vote.setQuestion(question);
        int totalVote = vote.getVote();
        vote.setVote(totalVote+1);
        voteRepository.save(vote);
        return getQuestionVotes(questionId);
    }

    public int getQuestionVotes(Long questionId) {
        return voteRepository.sumVotesByQuestionId(questionId);
    }
}
