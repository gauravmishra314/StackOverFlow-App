package com.springapp.stackoverflow.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dogaa4m7x",
                "api_key", "352413429519476",
                "api_secret", "XrRGiSDcdDHzKHXdJX23_vHJM"
        ));
    }
}