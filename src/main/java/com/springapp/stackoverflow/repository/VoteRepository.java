package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Answer;
import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.User;
import com.springapp.stackoverflow.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote , Long> {
    Vote findByUserAndQuestion(User user, Question question);

    // Find a vote by user and answer
    Vote findByUserAndAnswer(User user, Answer answer);

    // Sum votes for a question
    @Query("SELECT COALESCE(SUM(v.vote), 0) FROM Vote v WHERE v.question.id = :questionId")
    int sumVotesByQuestionId(@Param("questionId") Long questionId);

    // Sum votes for an answer
    @Query("SELECT COALESCE(SUM(v.vote), 0) FROM Vote v WHERE v.answer.id = :answerId")
    int sumVotesByAnswerId(@Param("answerId") Long answerId);
}
