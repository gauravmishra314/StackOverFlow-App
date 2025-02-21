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
        if (file == null || file.isEmpty()) {
            logger.warn("Empty file provided for upload");
            return null;
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "stackoverflow",
                            "resource_type", "auto"));

            if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                logger.error("Upload failed, uploadResult: {}", uploadResult);
                throw new RuntimeException("Upload failed, no secure_url returned from Cloudinary.");
            }

            // Return the secure_url instead of public_id
            String secureUrl = (String) uploadResult.get("secure_url");
            logger.info("Successfully uploaded to Cloudinary. Secure URL: {}", secureUrl);
            return secureUrl;
        } catch (Exception e) {
            logger.error("Error uploading to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

}