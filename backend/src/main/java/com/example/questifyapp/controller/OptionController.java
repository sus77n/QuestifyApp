package com.example.questifyapp.controller;

import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.OptionRepository;
import com.example.questifyapp.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/options")
public class OptionController {
    @Autowired
    private OptionService optionService;

    @GetMapping("")
    public List<Option> getAllOptions() {
        return optionService.getAllOptions();
    }

}
