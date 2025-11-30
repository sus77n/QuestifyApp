package com.example.iquiz.dto.attemptDetail;

import java.math.BigDecimal;
import java.util.UUID;

public record AttemptDetailDto(
        UUID id,
        UUID exerciseId,
        String userAnswerJson,
        BigDecimal score
) {
}
