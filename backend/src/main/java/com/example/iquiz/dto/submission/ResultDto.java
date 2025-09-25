package com.example.iquiz.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    Long exerciseId;
    String userAnswer;
    String correctAnswer;
    BigDecimal score;
}
