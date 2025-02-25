package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.model.User;

public interface VoteService {
    /**
     * Vote on a question
     * @param questionId the ID of the question
     * @param voteType 1 for upvote, -1 for downvote
     * @param currentUser the user voting
     * @return the new vote count
     */
    int voteQuestion(Long questionId, int voteType, User currentUser);

    /**
     * Vote on an answer
     * @param answerId the ID of the answer
     * @param voteType 1 for upvote, -1 for downvote
     * @param currentUser the user voting
     * @return the new vote count
     */
    int voteAnswer(Long answerId, int voteType, User currentUser);

    /**
     * Get the total votes for a question
     * @param questionId the ID of the question
     * @return the vote count
     */
    int getQuestionVotes(Long questionId);

    /**
     * Get the total votes for an answer
     * @param answerId the ID of the answer
     * @return the vote count
     */
    int getAnswerVotes(Long answerId);
}