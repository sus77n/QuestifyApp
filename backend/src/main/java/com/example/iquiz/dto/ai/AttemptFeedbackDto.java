package com.example.iquiz.dto.ai;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttemptFeedbackDto {
    String overallFeedback;
    String strengths;
    String weaknesses;
    String recommendations;
}
