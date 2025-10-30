package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.service.MarkdownReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/markdown")
public class MarkdownController {
    @Autowired
    private MarkdownReaderUtil markdownReaderUtil;

    @GetMapping("/read")
    public ApiResponse<String> readMarkdown(
            @RequestParam String filename,
            @RequestParam Long userId) {
        markdownReaderService.parseAndSaveMarkdown(fullPath, userId);
        return ApiResponse.success(null, "Markdown read successfully");
    }
}
