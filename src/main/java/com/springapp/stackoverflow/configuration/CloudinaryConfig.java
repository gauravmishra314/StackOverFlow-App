package com.springapp.stackoverflow.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqmjfe5mg",
                "api_key", "385184643429143",
                "api_secret", "8_Jzppl9n8TStVYgLQxVUPGXE4"
        ));
    }
}
