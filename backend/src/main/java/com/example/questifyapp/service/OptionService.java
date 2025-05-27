package com.example.questifyapp.service;

import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;

    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    public Option getOptionById(Long id) {
        return optionRepository.findById(id).orElse(null);
    }

    public void addOption(Option option) {
        optionRepository.save(option);
    }

    public void updateOption(Option option) {
        optionRepository.save(option);
    }

    public void deleteOptionById(Long id) {
        optionRepository.deleteById(id);
    }

    public Long totalOptions() {
        return optionRepository.count();
    }

}
