package com.example.iquiz.dto.attemptDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    UUID exerciseId;
    String question;
    String exerciseType;
    List<String> userAnswer;
    List<String> expectedAnswer;
    BigDecimal score;
}