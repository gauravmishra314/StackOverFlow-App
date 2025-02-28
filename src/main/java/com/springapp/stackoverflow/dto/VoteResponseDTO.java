package com.springapp.stackoverflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDTO {
    private Long id;
    private int totalVotes;
    private String message;
    private Boolean success;
}
