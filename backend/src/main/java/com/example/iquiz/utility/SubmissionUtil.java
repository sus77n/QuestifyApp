package com.example.iquiz.utility;

import com.example.iquiz.dto.answer.MatchingPair;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class SubmissionUtil {
    private static final BigDecimal FULL_SCORE = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO_SCORE = BigDecimal.ZERO;
    private static final double MINIMUM_MATCH_THRESHOLD = 0.5;


    public BigDecimal calculateScore(Exercise exercise, String userAnswerJson) {
        String correctJson = exercise.getCorrectAnswerJson();
        if (userAnswerJson == null || correctJson == null) return ZERO_SCORE;

        try {
            return switch (exercise.getType()) {
                case MULTIPLE_CHOICE -> scoreMultipleChoice(userAnswerJson, correctJson);
                case SELECT_MULTIPLE, TRUE_FALSE -> scoreSelectMultiple(userAnswerJson, correctJson);
                case SHORT_ANSWER -> scoreShortAnswer(userAnswerJson, correctJson);
                case MATCHING -> scoreMatching(userAnswerJson, correctJson);
                case REORDERING -> scoreReordering(userAnswerJson, correctJson);
                case FILL_IN_THE_BLANK -> scoreFillInBlank(userAnswerJson, correctJson);
                default -> ZERO_SCORE;
            };
        } catch (Exception e) {
            logCalculationError(exercise.getId(), userAnswerJson, correctJson, e);
            return ZERO_SCORE;
        }
    }

    public List<String> parseAnswers(String answerJson, Exercise exercise) {
        ExerciseType type = exercise.getType();
        switch (type) {
            case MULTIPLE_CHOICE, SELECT_MULTIPLE, TRUE_FALSE -> {
                List<String> optionIds = ExerciseTypeUtil.parseToList(answerJson);
                List<String> result = exercise.getPredefinedAnswers().stream()
                        .filter(answer -> optionIds.contains(answer.getHeader()))
                        .map(Answer::getText)
                        .toList();
                return result;
            }
            case SHORT_ANSWER, FILL_IN_THE_BLANK -> {
                return ExerciseTypeUtil.parseToList(answerJson);
            }
            case MATCHING -> {
                List<MatchingPair> pairs = ExerciseTypeUtil.parseMatchingPairs(answerJson);

                Map<String, Answer> leftMap = new HashMap<>();
                Map<String, Answer> rightMap = new HashMap<>();

                for (Answer a : exercise.getPredefinedAnswers()) {
                    String meta = a.getMetadata();
                    if (meta != null && meta.contains("left")) {
                        leftMap.put(a.getHeader(), a);
                    } else if (meta != null && meta.contains("right")) {
                        rightMap.put(a.getHeader(), a);
                    }
                }

                List<String> output = new ArrayList<>();

                for (MatchingPair pair : pairs) {
                    Answer leftAns = leftMap.get(pair.getLeftHeader());
                    Answer rightAns = rightMap.get(pair.getRightHeader());

                    if (leftAns == null || rightAns == null) {
                        throw new ResourceNotFoundException(
                                "Matching Answer",
                                "Header",
                                leftAns == null ? pair.getLeftHeader() : pair.getRightHeader()
                        );
                    }

                    output.add(leftAns.getText() + " -> " + rightAns.getText());
                }

                return output;
            }

            case REORDERING -> {
                List<String> ids = ExerciseTypeUtil.parseToList(answerJson);
                List<String> result = ids.stream().map(header -> {
                    for (Answer ans : exercise.getPredefinedAnswers()) {
                        if (ans.getHeader().equals(String.valueOf(header))) {
                            return ans.getText();
                        }
                    }
                    return "";
                }).toList();
                return result;
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }

    private BigDecimal scoreMultipleChoice(String userJson, String correctJson) {
        List<String> userHeaders = ExerciseTypeUtil.parseToList(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseToList(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;

        if (userHeaders.size() != 1) {
            return ZERO_SCORE;
        }

        String userChoice = userHeaders.get(0);
        return correctHeaders.contains(userChoice) ? FULL_SCORE : ZERO_SCORE;
    }


    private BigDecimal scoreSelectMultiple(String userJson, String correctJson) {
        List<String> userHeaders = ExerciseTypeUtil.parseToList(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseToList(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;

        Set<String> userSet = new HashSet<>(userHeaders);
        Set<String> correctSet = new HashSet<>(correctHeaders);

        if (userSet.equals(correctSet)) return FULL_SCORE;

        long correctSelections = userSet.stream()
                .filter(correctSet::contains)
                .count();

        long incorrectSelections = userSet.size() - correctSelections;
        int totalCorrect = correctSet.size();

        if (totalCorrect == 0) {
            return ZERO_SCORE;
        }

        double raw = (double) (correctSelections - incorrectSelections) / totalCorrect;

        double ratio = Math.max(0.0, Math.min(1.0, raw));

        return BigDecimal.valueOf(ratio * 100).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal scoreShortAnswer(String userJson, String correctJson) {
        List<String> userAnswers = ExerciseTypeUtil.parseToList(userJson);
        List<String> correctAnswers = ExerciseTypeUtil.parseToList(correctJson);

        if (userAnswers.isEmpty() || correctAnswers.isEmpty()) return ZERO_SCORE;

        String userText = userAnswers.get(0).trim();

        // Exact match for any acceptable answer
        for (String correct : correctAnswers) {
            if (userText.equalsIgnoreCase(correct.trim())) {
                return FULL_SCORE;
            }
        }

        // Partial credit: pick the best match among all acceptable answers
        BigDecimal best = ZERO_SCORE;
        for (String correct : correctAnswers) {
            BigDecimal candidate = partialShortAnswerScore(userText, correct);
            if (candidate.compareTo(best) > 0) {
                best = candidate;
            }
        }

        return best;
    }

    private BigDecimal partialShortAnswerScore(String userText, String correctText) {
        String user = userText.toLowerCase().trim();
        String correct = correctText.toLowerCase().trim();

        if (user.equals(correct)) return FULL_SCORE;

        String[] userWords = user.split("\\s+");
        String[] correctWords = correct.split("\\s+");

        Set<String> correctSet = new HashSet<>(Arrays.asList(correctWords));
        int matched = 0;

        for (String w : userWords) {
            if (w.length() > 3 && correctSet.contains(w)) {
                matched++;
            }
        }

        double ratio = (double) matched / correctWords.length;

        if (ratio >= MINIMUM_MATCH_THRESHOLD) {
            return BigDecimal.valueOf(ratio * 100).setScale(2, RoundingMode.HALF_UP);
        }

        return ZERO_SCORE;
    }

    private BigDecimal scoreMatching(String userJson, String correctJson) {

        List<MatchingPair> userPairs =
                ExerciseTypeUtil.parseMatchingPairs(userJson);

        List<MatchingPair> correctPairs =
                ExerciseTypeUtil.parseMatchingPairs(correctJson);

        if (userPairs.isEmpty() || correctPairs.isEmpty()) return ZERO_SCORE;

        Map<String, String> correctMap = new HashMap<>();
        for (MatchingPair p : correctPairs) {
            correctMap.put(p.getLeftHeader(), p.getRightHeader());
        }

        int correct = 0;
        for (MatchingPair u : userPairs) {
            if (Objects.equals(correctMap.get(u.getLeftHeader()), u.getRightHeader())) {
                correct++;
            }
        }

        return BigDecimal.valueOf((correct * 100.0) / correctPairs.size())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal scoreReordering(String userJson, String correctJson) {

        List<String> userHeaders = ExerciseTypeUtil.parseToList(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseToList(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;
        if (userHeaders.size() != correctHeaders.size()) return ZERO_SCORE;

        int correctCount = 0;
        for (int i = 0; i < userHeaders.size(); i++) {
            if (userHeaders.get(i).equals(correctHeaders.get(i))) correctCount++;
        }

        return BigDecimal.valueOf(correctCount * 100.0 / correctHeaders.size())
                .setScale(2, RoundingMode.HALF_UP);
    }


    private BigDecimal scoreFillInBlank(String userJson, String correctJson) {
        List<String> userAnswers = ExerciseTypeUtil.parseToList(userJson);
        List<String> correctAnswers = ExerciseTypeUtil.parseToList(correctJson);

        if (userAnswers.isEmpty() || correctAnswers.isEmpty()) return ZERO_SCORE;

        int correct = 0;
        for (int i = 0; i < Math.min(userAnswers.size(), correctAnswers.size()); i++) {
            if (userAnswers.get(i).trim().equalsIgnoreCase(correctAnswers.get(i).trim())) {
                correct++;
            }
        }

        return BigDecimal.valueOf(correct * 100.0 / correctAnswers.size())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void logCalculationError(UUID exerciseId, String userAnswer, String correctAnswer, Exception e) {
        System.out.println("Unable to calculate score for exercise id: " + exerciseId);
        System.out.println("User answer: " + userAnswer);
        System.out.println("Correct answer: " + correctAnswer);
        System.out.println("Error: " + e.getMessage());
    }

    public Double calculateAverageScore(BigDecimal totalScore, int submissionCount) {
        if (submissionCount == 0) return 0.0;
        return totalScore.divide(BigDecimal.valueOf(submissionCount), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}