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

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.getOptionById(id));
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<OptionResponseDto>> getByExerciseId(@PathVariable Long exerciseId) {
        return ResponseEntity.ok(optionService.getOptionsByExerciseId(exerciseId));
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

