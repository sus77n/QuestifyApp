package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.answer.AnswerRequestDto;
import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/options")
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @GetMapping("/{id}")
    public ApiResponse<OptionDto> getById(@PathVariable UUID id) {
        return ApiResponse.success(answerService.getOptionById(id), "Option retrieved successfully");
    }

    @GetMapping("/exercise/{exerciseId}")
    public ApiResponse<List<OptionDto>> getByExerciseId(@PathVariable UUID exerciseId) {
        return ApiResponse.success(answerService.getOptionsByExerciseId(exerciseId), "Options retrieved successfully");
    }

    @PostMapping
    public ApiResponse<OptionDto> create(@RequestBody AnswerRequestDto dto) {
        return ApiResponse.success(answerService.saveOption(dto), "Option created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<OptionDto> update(@PathVariable UUID id, @RequestBody AnswerRequestDto dto) {
        return ApiResponse.success(answerService.updateOption(id, dto), "Option updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        answerService.deleteOptionById(id);
        return ApiResponse.success(null, "Option has been deleted!");
    }
}

