package com.example.iquiz.utility;

import com.example.iquiz.dto.answer.MatchingPair;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ApiException;
import com.example.iquiz.exception.ConflictException;
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
            throw new ConflictException("Failed to create correct answer JSON");
        }
    }

    // ==================== PARSING METHODS ====================
    public static List<Object> parseAnswers(String json) {
        try {
            if (json == null || json.equalsIgnoreCase("null") || json.isBlank()) {
                return List.of();
            }

            json = sanitizeJson(json);

            JsonNode root = safeReadTree(json);
            if (root == null) return List.of();

            if (root.isArray()) {
                return objectMapper.convertValue(root, new TypeReference<List<Object>>() {});
            }

            if (root.isObject()) {
                JsonNode answersNode = root.get("correctAnswers");
                if (answersNode != null && answersNode.isArray()) {
                    return objectMapper.convertValue(
                            answersNode,
                            new TypeReference<List<Object>>() {}
                    );
                }
            }

            if (root.isValueNode()) {
                return List.of(objectMapper.convertValue(root, Object.class));
            }

            return List.of();

        } catch (Exception e) {
            return List.of();
        }
    }

    public static List<String> parseToList(String json) {
        return parseAnswers(json).stream()
                .filter(Objects::nonNull)
                .filter(v -> v instanceof String || v instanceof Number)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static List<MatchingPair> parseMatchingPairs(String json) {
        if (json == null || json.isBlank()) return List.of();

        try {
            json = sanitizeJson(json);

            JsonNode root = objectMapper.readTree(json);

            JsonNode arrayNode;

            if (root.isArray()) {
                arrayNode = root;
            } else if (root.has("correctAnswers")) {
                arrayNode = root.get("correctAnswers");
            } else {
                return List.of();
            }

            List<MatchingPair> result = new ArrayList<>();

            for (JsonNode node : arrayNode) {
                result.add(new MatchingPair(
                        node.get("leftHeader").asText(),
                        node.get("rightHeader").asText()
                ));
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

    public static JsonNode safeReadTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            try {
                String fixed = json.trim();

                if (fixed.startsWith("{") && !fixed.endsWith("}")) {
                    fixed += "}";
                }
                if (fixed.contains("[") && !fixed.endsWith("]")) {
                    fixed += "]";
                }

                return objectMapper.readTree(fixed);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private static String sanitizeJson(String json) {
        json = json.trim();

        if (json.startsWith("{") && !json.endsWith("}")) {
            json = json + "]}";
        }

        if (json.startsWith("[") && !json.endsWith("]")) {
            json = json + "]";
        }

        return json;
    }
}
