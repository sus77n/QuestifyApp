package com.example.iquiz.config;

import com.example.iquiz.enums.ExerciseType;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class ExerciseTypeConfig {

    private final Map<ExerciseType, ExerciseTypeProperties> typeProperties;

    public ExerciseTypeConfig() {
        typeProperties = new EnumMap<>(ExerciseType.class);

        // Define properties for each exercise type
        typeProperties.put(ExerciseType.MULTIPLE_CHOICE,
                new ExerciseTypeProperties(true, false, "Single choice question"));
        typeProperties.put(ExerciseType.SELECT_MULTIPLE,
                new ExerciseTypeProperties(true, false, "Multiple choice question"));
        typeProperties.put(ExerciseType.TRUE_FALSE,
                new ExerciseTypeProperties(true, false, "True/False question"));
        typeProperties.put(ExerciseType.SHORT_ANSWER,
                new ExerciseTypeProperties(false, true, "Short text answer"));
        typeProperties.put(ExerciseType.MATCHING,
                new ExerciseTypeProperties(true, false, "Matching pairs"));
        typeProperties.put(ExerciseType.REORDERING,
                new ExerciseTypeProperties(false, true, "Reorder items"));
        typeProperties.put(ExerciseType.FILL_IN_THE_BLANK,
                new ExerciseTypeProperties(false, true, "Fill in blanks"));

        // ADD NEW EXERCISE TYPES HERE:
        // typeProperties.put(ExerciseType.NEW_TYPE,
        //     new ExerciseTypeProperties(requiresOptions, hasTextInput, description));
    }

    public ExerciseTypeProperties getProperties(ExerciseType type) {
        return typeProperties.get(type);
    }

    public Set<ExerciseType> getSupportedTypes() {
        return typeProperties.keySet();
    }

    public static class ExerciseTypeProperties {
        private final boolean requiresOptions;
        private final boolean hasTextInput;
        private final String description;

        public ExerciseTypeProperties(boolean requiresOptions, boolean hasTextInput, String description) {
            this.requiresOptions = requiresOptions;
            this.hasTextInput = hasTextInput;
            this.description = description;
        }

        // Getters
        public boolean requiresOptions() { return requiresOptions; }
        public boolean hasTextInput() { return hasTextInput; }
        public String getDescription() { return description; }
    }
}