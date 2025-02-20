package com.springapp.stackoverflow.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.springapp.stackoverflow.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        System.out.println("I am inside the uploadImage---" + file);
        if (file == null || file.isEmpty()) {
            logger.warn("Empty file provided for upload. Filename: {}, Size: {}",
                    file != null ? file.getOriginalFilename() : "null",
                    file != null ? file.getSize() : 0);
            return null;
        }

        logger.info("Attempting to upload file: {}, Size: {}",
                file.getOriginalFilename(), file.getSize());

        try {
            String folderName = "stackoverflow-clone" ;
            System.out.println("Cloudinary API Key: " + cloudinary.config.apiKey);
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folderName,
                    "resource_type", "auto"
            );
            byte[] bytes = file.getBytes();
            Map<String, Object> uploadResult = cloudinary.uploader().upload(bytes,
                    ObjectUtils.asMap(
                            "folder", "stackoverflow",
                            "resource_type", "auto"));

            if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                logger.error("Upload failed, uploadResult: {}", uploadResult);
                throw new RuntimeException("Upload failed, no secure_url returned from Cloudinary.");
            }
            logger.info("uploadResult",uploadResult);
            String secureUrl = (String) uploadResult.get("public_id");
            logger.info("this is my url---",cloudinary.url().secure(true).generate("public_id"));

            logger.info("Successfully uploaded to Cloudinary. Secure URL: {}", secureUrl);
            return "https://res.cloudinary.com/dqmjfe5mg/image/upload/stackoverflow/"+secureUrl;
        } catch (Exception e) {
            logger.error("Error uploading to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

}