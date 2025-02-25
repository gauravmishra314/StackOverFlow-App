package com.springapp.stackoverflow.service;


import com.springapp.stackoverflow.dto.CommentDTO;

public interface CommentService {
    CommentDTO save(Long id, CommentDTO commentDto, String question);

    void delete(Long targetID, Long deleteID,  String typeReq);

    CommentDTO edit(Long answerID, Long commentID, CommentDTO commentDto);

}
