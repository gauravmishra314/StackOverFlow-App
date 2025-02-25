package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.model.Vote;

public interface VoteService {
   // Vote voteQuestion(Long questionId, String voterKey, int voteType);
   // int getTotalVotesForQuestion(Long questionId);
   // int getCurrentVoteForQuestion(Long questionId, String voterKey);
    int getQuestionVotes(Long questionId);
    int voteQuestion(Long questionId, int voteType);
}
