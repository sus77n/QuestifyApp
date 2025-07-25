package com.example.iquiz.controller;

import com.example.iquiz.dto.option.OptionRequestDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {
    @Autowired
    private OptionService optionService;

    @GetMapping
    public ResponseEntity<List<OptionResponseDto>> findAll() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> create(@RequestBody OptionRequestDto dto) {
        return ResponseEntity.ok(optionService.saveOption(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionResponseDto> update(@PathVariable Long id, @RequestBody OptionRequestDto dto) {
        return ResponseEntity.ok(optionService.updateOption(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        optionService.deleteOptionById(id);
        return ResponseEntity.ok("Option has been deleted!");
    }

}
