package com.example.iquiz.service;

import com.example.iquiz.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Service
public class MarkdownService {


    public String markdownToString(File markdown) {
        try (BufferedReader reader = new BufferedReader(new FileReader(markdown))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConflictException("markdown to string error");
        }
    }
}
