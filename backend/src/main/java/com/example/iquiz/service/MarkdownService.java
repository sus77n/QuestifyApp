package com.example.iquiz.service;

import com.example.iquiz.enums.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MarkdownService {

    public String loadPrompt(PromptTemplate name) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + name.getFileName());
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load prompt: " + name, e);
        }
    }
}
