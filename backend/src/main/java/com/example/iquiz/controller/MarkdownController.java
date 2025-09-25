package com.example.iquiz.controller;

import com.example.iquiz.utility.MarkdownReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/markdown")
public class MarkdownController {
    @Autowired
    private MarkdownReaderUtil markdownReaderUtil;

    @GetMapping("/read")
    public ResponseEntity<String> readMarkdown(
            @RequestParam String filename,
            @RequestParam Long userId) {
        markdownReaderUtil.parseAndSaveMarkdown(filename, userId);
        return ResponseEntity.ok("Imported successfully");
    }
}
