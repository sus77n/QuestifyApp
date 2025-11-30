package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.utility.MarkdownReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/markdown")
public class MarkdownController {
    @Autowired
    private MarkdownReaderUtil markdownReaderUtil;

    @GetMapping("/read")
    public ApiResponse<String> readMarkdown(
            @RequestParam String filename,
            @RequestParam UUID userId) {
        markdownReaderUtil.parseAndSaveMarkdown(filename, userId);
        return ApiResponse.success(null, "Markdown read successfully");
    }
}
