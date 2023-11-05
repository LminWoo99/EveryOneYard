package com.example.VideoChatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://52.78.190.79:8080/",
                        "http://everyoneyard.shop/",
                        "https://52.78.190.79:8080/",
                        "https://everyoneyard.shop/"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
        ;

    }

}