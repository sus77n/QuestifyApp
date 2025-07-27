package com.example.iquiz.utility;

import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Option;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SubmissionUtil {
    private static final BigDecimal FULL_SCORE = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO_SCORE = BigDecimal.ZERO;
    private static final double MINIMUM_MATCH_THRESHOLD = 0.5;

    public BigDecimal calculateScore(SubmissionDto submission, Exercise exercise, Option selectedOption) {
        if (submission.selectedOptionId() != null && selectedOption != null) {
            return calculateMultipleChoiceScore(selectedOption);
        } else if (submission.answer() != null && exercise.getAnswer() != null) {
            return calculateTextAnswerScore(submission.answer(), exercise.getAnswer());
        }
        return ZERO_SCORE;
    }

    private BigDecimal calculateMultipleChoiceScore(Option selectedOption) {
        return selectedOption.isCorrect() ? FULL_SCORE : ZERO_SCORE;
    }

    private BigDecimal calculateTextAnswerScore(String submittedAnswer, String correctAnswer) {
        String normalizedSubmission = submittedAnswer.toLowerCase().trim();
        String normalizedCorrect = correctAnswer.toLowerCase().trim();

        if (normalizedSubmission.equals(normalizedCorrect)) {
            return FULL_SCORE;
        }

        String[] correctParts = normalizedCorrect.split("\\s+");
        int matchedParts = 0;

        for (String part : correctParts) {
            if (part.length() > 3 && normalizedSubmission.contains(part)) {
                matchedParts++;
            }
        }

        double matchRatio = (double) matchedParts / correctParts.length;
        if (matchRatio >= MINIMUM_MATCH_THRESHOLD) {
            return BigDecimal.valueOf(matchRatio * 100).setScale(2, RoundingMode.HALF_UP);
        }

        return ZERO_SCORE;
    }

    public Double calculateAverageScore(BigDecimal totalScore, int submissionCount) {
        if (submissionCount == 0) return 0.0;
        return totalScore.divide(BigDecimal.valueOf(submissionCount), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}