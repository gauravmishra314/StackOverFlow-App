package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote , Long> {
    //Vote findByQuestionIdAndVoterKey(Long questionId, String voterKey);
    @Query("SELECT COALESCE(SUM(v.vote), 0) FROM Vote v WHERE v.question.id = :questionId")
    int sumVotesByQuestionId(@Param("questionId") Long questionId);
}
