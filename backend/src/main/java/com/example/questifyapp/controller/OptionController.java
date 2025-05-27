package com.example.questifyapp.controller;

import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.OptionRepository;
import com.example.questifyapp.service.OptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/options")
public class OptionController {
    @Autowired
    private OptionService optionService;
    @Autowired
    private OptionRepository optionRepository;

    @GetMapping("")
    public List<OptionDTO> getAllOptions() {
        return optionService.getAllOptions().stream()
                .map(OptionDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionDTO> getOption(@PathVariable Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        return ResponseEntity.ok(OptionDTO.fromEntity(option));
    }

}
