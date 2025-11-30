package com.example.iquiz.utility;

import com.example.iquiz.entity.Answer;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ConflictException;
import com.example.iquiz.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

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
                case SELECT_MULTIPLE, TRUE_FALSE -> scoreSelectMultiple(exercise, userAnswerJson, correctJson);
                case SHORT_ANSWER -> scoreShortAnswer(userAnswerJson, correctJson);
                case MATCHING -> scoreMatching(userAnswerJson, correctJson);
                case REORDERING -> scoreReordering(userAnswerJson, correctJson);
                case FILL_IN_THE_BLANK -> scoreFillInBlank(userAnswerJson, correctJson);
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
                List<String> optionIds = ExerciseTypeUtil.parseOptionHeaders(answerJson);
                List<String> result = exercise.getPredefinedAnswers().stream()
                        .filter(answer -> optionIds.contains(answer.getHeader()))
                        .map(Answer::getText)
                        .toList();
                return result;
            }
            case SHORT_ANSWER, FILL_IN_THE_BLANK -> {
                return ExerciseTypeUtil.parseTextAnswers(answerJson);
            }
            case MATCHING -> {
                List<ExerciseTypeUtil.MatchingPair> pairs = ExerciseTypeUtil.parseMatchingPairs(answerJson);

                Map<String, Answer> leftMap = new HashMap<>();
                Map<String, Answer> rightMap = new HashMap<>();

                for (Answer a : exercise.getPredefinedAnswers()) {
                    String meta = a.getMetadata();
                    if (meta != null && meta.contains("\"side\":\"left\"")) {
                        leftMap.put(a.getHeader(), a);
                    } else if (meta != null && meta.contains("\"side\":\"right\"")) {
                        rightMap.put(a.getHeader(), a);
                    }
                }

                List<String> output = new ArrayList<>();

                for (ExerciseTypeUtil.MatchingPair pair : pairs) {
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
                List<String> ids = ExerciseTypeUtil.parseOptionHeaders(answerJson);
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

    // ---------------------------------------------------------
    // MULTIPLE CHOICE
    // ---------------------------------------------------------
    private BigDecimal scoreMultipleChoice(String userJson, String correctJson) {

        List<String> userHeaders = ExerciseTypeUtil.parseOptionHeaders(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseOptionHeaders(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;

        return correctHeaders.contains(userHeaders.get(0)) ? FULL_SCORE : ZERO_SCORE;
    }


    // ---------------------------------------------------------
    // SELECT MULTIPLE + TRUE / FALSE
    // ---------------------------------------------------------
    private BigDecimal scoreSelectMultiple(Exercise exercise, String userJson, String correctJson) {

        List<String> userHeaders = ExerciseTypeUtil.parseOptionHeaders(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseOptionHeaders(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;

        Set<String> userSet = new HashSet<>(userHeaders);
        Set<String> correctSet = new HashSet<>(correctHeaders);

        if (userSet.equals(correctSet)) return FULL_SCORE;

        long correctSelections = userSet.stream().filter(correctSet::contains).count();
        long incorrectSelections = userSet.size() - correctSelections;
        long missedSelections = correctSet.size() - correctSelections;

        double total = correctSet.size();
        double penalty = (incorrectSelections + missedSelections) * 0.5;
        double ratio = Math.max(0, total - penalty) / total;

        return BigDecimal.valueOf(ratio * 100).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------------------------------------------------------
    // SHORT ANSWER
    // ---------------------------------------------------------
    private BigDecimal scoreShortAnswer(String userJson, String correctJson) {

        List<String> userAnswers = ExerciseTypeUtil.parseTextAnswers(userJson);
        List<String> correctAnswers = ExerciseTypeUtil.parseTextAnswers(correctJson);

        if (userAnswers.isEmpty() || correctAnswers.isEmpty()) return ZERO_SCORE;

        String userText = userAnswers.get(0).trim();

        // Exact match for any acceptable answer
        for (String correct : correctAnswers) {
            boolean matches = userText.equalsIgnoreCase(correct.trim());
            if (matches) return FULL_SCORE;
        }

        // Partial credit
        return partialShortAnswerScore(userText, correctAnswers.get(0));
    }

    private BigDecimal partialShortAnswerScore(String userText, String correctText) {
        String user = userText.toLowerCase();
        String correct = correctText.toLowerCase();

        if (userText.equalsIgnoreCase(correctText)) return FULL_SCORE;

        String[] userWords = user.split("\\s+");
        String[] correctWords = correct.split("\\s+");

        Set<String> correctSet = new HashSet<>(Arrays.asList(correctWords));
        int matched = 0;

        for (String w : userWords) {
            if (w.length() > 3 && correctSet.contains(w)) matched++;
        }

        double ratio = (double) matched / correctWords.length;

        if (ratio >= MINIMUM_MATCH_THRESHOLD) {
            return BigDecimal.valueOf(ratio * 100).setScale(2, RoundingMode.HALF_UP);
        }

        return ZERO_SCORE;
    }

    // ---------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------
    private BigDecimal scoreMatching(String userJson, String correctJson) {

        List<ExerciseTypeUtil.MatchingPair> userPairs =
                ExerciseTypeUtil.parseMatchingPairs(userJson);

        List<ExerciseTypeUtil.MatchingPair> correctPairs =
                ExerciseTypeUtil.parseMatchingPairs(correctJson);

        if (userPairs.isEmpty() || correctPairs.isEmpty()) return ZERO_SCORE;

        Map<String, String> correctMap = new HashMap<>();
        for (ExerciseTypeUtil.MatchingPair p : correctPairs) {
            correctMap.put(p.getLeftHeader(), p.getRightHeader());
        }

        int correct = 0;
        for (ExerciseTypeUtil.MatchingPair u : userPairs) {
            if (Objects.equals(correctMap.get(u.getLeftHeader()), u.getRightHeader())) {
                correct++;
            }
        }

        return BigDecimal.valueOf((correct * 100.0) / correctPairs.size())
                .setScale(2, RoundingMode.HALF_UP);
    }


    // ---------------------------------------------------------
    // REORDERING
    // ---------------------------------------------------------
    private BigDecimal scoreReordering(String userJson, String correctJson) {

        List<String> userHeaders = ExerciseTypeUtil.parseOptionHeaders(userJson);
        List<String> correctHeaders = ExerciseTypeUtil.parseOptionHeaders(correctJson);

        if (userHeaders.isEmpty() || correctHeaders.isEmpty()) return ZERO_SCORE;
        if (userHeaders.size() != correctHeaders.size()) return ZERO_SCORE;

        int correctCount = 0;
        for (int i = 0; i < userHeaders.size(); i++) {
            if (userHeaders.get(i).equals(correctHeaders.get(i))) correctCount++;
        }

        return BigDecimal.valueOf(correctCount * 100.0 / correctHeaders.size())
                .setScale(2, RoundingMode.HALF_UP);
    }


    // ---------------------------------------------------------
    // FILL IN THE BLANK
    // ---------------------------------------------------------
    private BigDecimal scoreFillInBlank(String userJson, String correctJson) {
        List<String> userAnswers = ExerciseTypeUtil.parseBlankAnswers(userJson);
        List<String> correctAnswers = ExerciseTypeUtil.parseBlankAnswers(correctJson);

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

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------
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

    private Map<String, String> buildHeaderToIdMap(Exercise exercise) {
        Map<String, String> map = new HashMap<>();
        if (exercise.getPredefinedAnswers() == null) return map;

        for (Answer ans : exercise.getPredefinedAnswers()) {
            if (ans.getHeader() == null) continue;
            try {
                String header = ans.getHeader().trim();
                map.put(header, ans.getId().toString());
            } catch (NumberFormatException ignored) {
                throw new ConflictException("Invalid header format for answer ID: " + ans.getId());
            }
        }

        return map;
    }

    private List<String> mapHeadersToIds(List<String> headers, Exercise exercise) {
        Map<String, String> headerToId = buildHeaderToIdMap(exercise);
        List<String> result = new ArrayList<>();

        for (String header : headers) {
            String id = headerToId.get(header);
            if (id != null) {
                result.add(id);
            }
        }

        return result;
    }

    private List<ExerciseTypeUtil.MatchingPair> mapHeaderPairsToIdPairs(
            List<ExerciseTypeUtil.MatchingPair> headerPairs,
            Exercise exercise
    ) {
        Map<String, String> headerToId = buildHeaderToIdMap(exercise);
        List<ExerciseTypeUtil.MatchingPair> result = new ArrayList<>();

        for (ExerciseTypeUtil.MatchingPair p : headerPairs) {
            String leftId = headerToId.get(p.getLeftHeader());
            String rightId = headerToId.get(p.getRightHeader());

            if (leftId != null && rightId != null) {
                result.add(new ExerciseTypeUtil.MatchingPair(leftId, rightId));
            }
        }

        return result;
    }


}