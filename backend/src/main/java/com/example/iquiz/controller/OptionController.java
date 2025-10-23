package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.option.OptionRequestDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    @Autowired
    OptionService optionService;

    @GetMapping("/{id}")
    public ApiResponse<OptionResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(optionService.getOptionById(id), "Option retrieved successfully");
    }

    @GetMapping("/exercise/{exerciseId}")
    public ApiResponse<List<OptionResponseDto>> getByExerciseId(@PathVariable Long exerciseId) {
        return ApiResponse.success(optionService.getOptionsByExerciseId(exerciseId), "Options retrieved successfully");
    }

    @PostMapping
    public ApiResponse<OptionResponseDto> create(@RequestBody OptionRequestDto dto) {
        return ApiResponse.success(optionService.saveOption(dto), "Option created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<OptionResponseDto> update(@PathVariable Long id, @RequestBody OptionRequestDto dto) {
        return ApiResponse.success(optionService.updateOption(id, dto), "Option updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        optionService.deleteOptionById(id);
        return ApiResponse.success(null, "Option has been deleted!");
    }
}

