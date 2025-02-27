package com.springapp.stackoverflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long answerId;
    private int voteType; // 1 for upvote, -1 for downvote
}
