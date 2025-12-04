package com.example.iquiz.config;


import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public Client geminiClient() {
        return new Client();
    }
}