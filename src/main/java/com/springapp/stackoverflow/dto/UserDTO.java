package com.springapp.stackoverflow.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String profilePic;
}
