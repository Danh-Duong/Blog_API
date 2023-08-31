package com.example.Blog_API.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dc1ofh0uj",
                "api_key", "243944985143298",
                "api_secret", "YceYlpFiWNGOZanyt-33_68xezg",
                "secure", true));
    }

}
