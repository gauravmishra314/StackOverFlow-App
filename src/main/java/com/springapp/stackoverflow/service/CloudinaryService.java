package com.springapp.stackoverflow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
}
