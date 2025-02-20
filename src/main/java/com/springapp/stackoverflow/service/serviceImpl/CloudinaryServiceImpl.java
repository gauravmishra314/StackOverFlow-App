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
            // Create a unique folder name for each upload
            String folderName = "stackoverflow-clone/" + UUID.randomUUID().toString();

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto",
                            "unique_filename", true
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            logger.info("Successfully uploaded image to Cloudinary: {}", secureUrl);
            System.out.println(secureUrl);
            return secureUrl;

        } catch (IOException e) {
            logger.error("Failed to upload image to Cloudinary", e);
            throw new IOException("Failed to upload image: " + e.getMessage());
        }
    }
}