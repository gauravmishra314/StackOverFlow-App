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
                "cloud_name", "dqmjfe5mg",
                "api_key", "557489714383712",
                "api_secret", "UB5wcoL5l9abCGVviH8Wr3FgxDc"
        ));
    }
}