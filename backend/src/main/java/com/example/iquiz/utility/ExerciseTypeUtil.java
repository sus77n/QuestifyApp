package com.example.iquiz.utility;

import com.example.iquiz.enums.ExerciseType;
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
    public static String createDefaultAnswer(ExerciseType type, String rawCorrectAnswerJson) {
        switch (type) {
            case MATCHING -> {
                List<MatchingPair> pairs = parseMatchingPairs(rawCorrectAnswerJson);
                return createCorrectAnswer(pairs);
            }
            default -> {
                List<String> answers = parseToList(rawCorrectAnswerJson);
                return createCorrectAnswer(answers);
            }
        }
    }

    public static String createCorrectAnswer(List<?> correctAnswers) {
        try {
            Map<String, Object> answer = new HashMap<>();
            answer.put("correctAnswers", correctAnswers != null ? correctAnswers : List.of());
            return objectMapper.writeValueAsString(answer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create correct answer JSON", e);
        }
    }

    // ==================== PARSING METHODS ====================
    public static List<Object> parseAnswers(String json) {
        try {
            if (json == null || json.equalsIgnoreCase("null") || json.equals("")) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(json);
            if (root.isArray()) {
                return objectMapper.convertValue(root, new TypeReference<List<Object>>() {
                });
            }

            if (root.isObject()) {
                JsonNode answersNode = root.get("correctAnswers");
                if (answersNode != null && answersNode.isArray()) {
                    return objectMapper.convertValue(answersNode, new TypeReference<List<Object>>() {
                    });
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

    public static List<String> parseToList(String json) {
        return parseAnswers(json).stream()
                .filter(v -> v instanceof String)
                .map(v -> v.toString())
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

    public static String removeCorrectAnswerJson(String correctAnswerJson) {
        if (correctAnswerJson == null || correctAnswerJson.isBlank()) {
            return "[]";
        }

        try {
            JsonNode root = objectMapper.readTree(correctAnswerJson);

            if (root.has("correctAnswers")) {
                JsonNode arr = root.get("correctAnswers");

                if (arr.isArray()) {
                    return objectMapper.writeValueAsString(arr);
                }
            }

            return objectMapper.writeValueAsString(root);

        } catch (Exception e) {
            return "[\"" + correctAnswerJson.replace("\"", "\\\"") + "\"]";
        }
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
