package com.example.iquiz.utility;

import com.example.iquiz.exception.ApiException;
import com.example.iquiz.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExerciseTypeUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== DEFAULT CREATION METHODS ====================

    public static String createCorrectAnswer(List<?> correctAnswers) {
        return createCorrectAnswer(correctAnswers, null);
    }

    public static String createCorrectAnswer(List<?> correctAnswers, Map<String, Object> config) {
        try {
            Map<String, Object> answer = new HashMap<>();
            answer.put("correctAnswers", correctAnswers != null ? correctAnswers : List.of());
            if (config != null && !config.isEmpty()) {
                answer.put("config", config);
            }
            return objectMapper.writeValueAsString(answer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create correct answer JSON", e);
        }
    }

    // ==================== TYPE-SPECIFIC CREATION ====================

    // For MCQ, MULTI-SELECT, TRUE_FALSE (NOW USE REAL OPTION IDs)
    public static String createOptionAnswer(List<Long> correctOptionIds) {
        return createCorrectAnswer(correctOptionIds);
    }

    public static String createSingleOptionAnswer(Long correctOptionId) {
        return createCorrectAnswer(List.of(correctOptionId));
    }

    // SHORT ANSWER
    public static String createShortAnswer(String expectedText) {
        return createShortAnswer(List.of(expectedText));
    }

    public static String createShortAnswer(List<String> acceptableAnswers) {
        Map<String, Object> config = Map.of("caseSensitive", false);
        return createCorrectAnswer(acceptableAnswers, config);
    }

    // TRUE/FALSE NOW MUST RECEIVE optionId FROM CALLER
    public static String createTrueFalseAnswer(Long trueOrFalseOptionId) {   // UPDATED
        return createCorrectAnswer(List.of(trueOrFalseOptionId));
    }

    // MATCHING (uses leftId/rightId as REAL option IDs)
    public static String createMatchingAnswer(List<MatchingPair> pairs) {
        return createCorrectAnswer(pairs);
    }

    // REORDERING
    public static String createReorderAnswer(List<String> correctOrder) {
        return createCorrectAnswer(correctOrder);
    }

    // FILL-IN-THE-BLANK
    public static String createFillInBlankAnswer(List<String> blankAnswers) {
        return createCorrectAnswer(blankAnswers);
    }

    // ==================== PARSING METHODS ====================

    public static List<Object> parseAnswers(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                return objectMapper.convertValue(root, new TypeReference<List<Object>>() {});
            }

            if (root.isObject()) {
                JsonNode answersNode = root.get("correctAnswers");
                if (answersNode != null && answersNode.isArray()) {
                    return objectMapper.convertValue(answersNode, new TypeReference<List<Object>>() {});
                }
            }

            if (root.isValueNode()) {
                return List.of(objectMapper.convertValue(root, Object.class));
            }
            return List.of();
        } catch (Exception e) {
            throw new ApiException("Failed to parse correct answers", ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    // ==================== TYPE-SPECIFIC PARSING ====================

    public static List<String> parseOptionHeaders(String json) {
        return parseAnswers(json).stream()
                .filter(v -> v instanceof String)
                .map(v -> v.toString())
                .collect(Collectors.toList());
    }

    public static List<String> parseTextAnswers(String json) {
        return parseAnswers(json).stream()
                .filter(v -> v instanceof String)
                .map(v -> (String) v)
                .collect(Collectors.toList());
    }

    public static List<MatchingPair> parseMatchingPairs(String json) {
        try {
            List<Object> raw = parseAnswers(json);

            List<MatchingPair> result = new ArrayList<>();
            for (Object v : raw) {
                if (v instanceof Map<?, ?> map) {
                    Object left = map.get("leftHeader");
                    Object right = map.get("rightHeader");

                    if (left instanceof String && right instanceof String) {
                        MatchingPair p = new MatchingPair();
                        p.setLeftHeader(left.toString());
                        p.setRightHeader(right.toString());
                        result.add(p);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }


    public static List<String> parseReorderSequence(String json) {
        return parseOptionHeaders(json);
    }

    public static List<String> parseBlankAnswers(String json) {
        return parseTextAnswers(json);
    }

    // SUPPORT CLASS
    public static class MatchingPair {
        private String leftHeader;
        private String rightHeader;

        public MatchingPair() {
        }

        public MatchingPair(String leftHeader, String rightHeader) {
            this.leftHeader = leftHeader;
            this.rightHeader = rightHeader;
        }

        public String getLeftHeader() {
            return leftHeader;
        }

        public String getRightHeader() {
            return rightHeader;
        }

        public void setLeftHeader(String leftHeader) {
            this.leftHeader = leftHeader;
        }

        public void setRightHeader(String rightHeader) {
            this.rightHeader = rightHeader;
        }
    }
}
