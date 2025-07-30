package com.example.iquiz.service;

import com.example.iquiz.entity.*;
import com.example.iquiz.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MarkdownReaderService {

    private static final Logger logger = LoggerFactory.getLogger(MarkdownReaderService.class);

    private static final Pattern EXERCISE_PATTERN = Pattern.compile("^## EX (\\d+\\.\\d+\\.\\d+)");
    private static final Pattern HEADER_PATTERN = Pattern.compile("^## #(Course|Topic|Lesson) #([^#]+)(?: #(.+))?");
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\*\\*Question\\*\\*:\\s*(.+)$");
    private static final Pattern TYPE_PATTERN = Pattern.compile("^\\*\\*Type\\*\\*:\\s*(.+)$");
    private static final Pattern DIFFICULTY_PATTERN = Pattern.compile("^\\*\\*Difficulty\\*\\*:\\s*(\\d+)$");
    private static final Pattern SOLUTION_PATTERN = Pattern.compile("^\\*\\*Solution\\*\\*:\\s*(.+)$");
    private static final Pattern OPTION_PATTERN = Pattern.compile("^-\\s+([A-Z])\\.\\s+(.+)$");

    private final LearningUnitRepository learningUnitRepository;
    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final ExerciseRepository exerciseRepository;
    private final OptionRepository optionRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final UserRepository userRepository;

    public MarkdownReaderService(LearningUnitRepository learningUnitRepository,
                                 LearningUnitTypeRepository learningUnitTypeRepository,
                                 ExerciseRepository exerciseRepository,
                                 OptionRepository optionRepository,
                                 ExerciseTypeRepository exerciseTypeRepository,
                                 UserRepository userRepository) {
        this.learningUnitRepository = learningUnitRepository;
        this.learningUnitTypeRepository = learningUnitTypeRepository;
        this.exerciseRepository = exerciseRepository;
        this.optionRepository = optionRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ImportResult parseAndSaveMarkdown(String markdownPath, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found with ID: " + creatorId));

        ImportResult result = new ImportResult();
        try (BufferedReader reader = new BufferedReader(new FileReader(markdownPath))) {
            LearningUnit currentCourse = null;
            LearningUnit currentTopic = null;
            LearningUnit currentLesson = null;
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Matcher headerMatcher = HEADER_PATTERN.matcher(line);
                if (headerMatcher.matches()) {
                    String headerType = headerMatcher.group(1);
                    String name = headerMatcher.group(2).trim();
                    String code = headerMatcher.group(3) != null ? headerMatcher.group(3).trim() : null;

                    switch (headerType) {
                        case "Course" -> {
                            currentCourse = findOrCreateLearningUnit(name, "COURSE", null, creator, code);
                            currentTopic = null;
                            currentLesson = null;
                            result.incrementCoursesProcessed();
                        }
                        case "Topic" -> {
                            if (currentCourse == null)
                                throw new IllegalStateException("Topic defined without a Course context");
                            currentTopic = findOrCreateLearningUnit(name, "TOPIC", currentCourse, creator, code);
                            currentLesson = null;
                            result.incrementTopicsProcessed();
                        }
                        case "Lesson" -> {
                            if (currentTopic == null)
                                throw new IllegalStateException("Lesson defined without a Topic context");
                            currentLesson = findOrCreateLearningUnit(name, "LESSON", currentTopic, creator, code);
                            result.incrementLessonsProcessed();
                        }
                    }
                    continue;
                }

                Matcher exerciseMatcher = EXERCISE_PATTERN.matcher(line);
                if (exerciseMatcher.matches() && currentLesson != null) {
                    String exerciseCode = exerciseMatcher.group(1);
                    try {
                        Exercise exercise = parseExercise(reader, exerciseCode);
                        exercise.setParent(currentLesson);
                        exerciseRepository.save(exercise);
                        result.incrementExercisesProcessed();
                    } catch (ExerciseParseException e) {
                        logger.error("Failed to parse exercise {}: {}", exerciseCode, e.getMessage());
                        result.addError("[" + exerciseCode + "] " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading markdown content", e);
        }
        return result;
    }

    private LearningUnit findOrCreateLearningUnit(String name, String typeName, LearningUnit parent, User creator, String code) {
        LearningUnitType type = learningUnitTypeRepository.findByName(typeName);
        if (type == null) {
            LearningUnitType newType = new LearningUnitType();
            newType.setName(typeName);
            newType.setLevel(getLevelForType(typeName));
            type = learningUnitTypeRepository.save(newType);
        }

        Optional<LearningUnit> existing = learningUnitRepository.findByNameAndTypeId(name, type.getId());
        if (existing.isPresent()) {
            LearningUnit unit = existing.get();
            if (code != null && !code.equals(unit.getCode())) {
                unit.setCode(code);
                learningUnitRepository.save(unit);
            }
            return unit;
        }

        LearningUnit newUnit = new LearningUnit();
        newUnit.setName(name);
        newUnit.setType(type);
        newUnit.setParent(parent);
        newUnit.setCreatedBy(creator);
        newUnit.setCode(code);
        return learningUnitRepository.save(newUnit);
    }

    private int getLevelForType(String typeName) {
        return switch (typeName) {
            case "COURSE" -> 1;
            case "TOPIC" -> 2;
            case "LESSON" -> 3;
            default -> 0;
        };
    }

    private Exercise parseExercise(BufferedReader reader, String exerciseCode) throws IOException, ExerciseParseException {
        Exercise exercise = new Exercise();
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now());

//      exerciseTypeRepository.findByCode(exerciseCode).ifPresent(exercise::setExerciseCode);

        List<Option> options = new ArrayList<>();
        String line;
        String correctAnswer = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.equals("---")) break;

            Matcher questionMatcher = QUESTION_PATTERN.matcher(line);
            if (questionMatcher.matches()) {
                exercise.setQuestion(questionMatcher.group(1).trim());
                continue;
            }

            Matcher typeMatcher = TYPE_PATTERN.matcher(line);
            if (typeMatcher.matches()) {
                exercise.setType(mapExerciseType(typeMatcher.group(1).trim()));
                continue;
            }

            Matcher difficultyMatcher = DIFFICULTY_PATTERN.matcher(line);
            if (difficultyMatcher.matches()) {
                exercise.setDifficulty(Integer.parseInt(difficultyMatcher.group(1)));
                continue;
            }

            Matcher solutionMatcher = SOLUTION_PATTERN.matcher(line);
            if (solutionMatcher.matches()) {
                correctAnswer = solutionMatcher.group(1).trim();
                continue;
            }

            Matcher optionMatcher = OPTION_PATTERN.matcher(line);
            if (optionMatcher.matches()) {
                String label = optionMatcher.group(1).trim();
                String text = optionMatcher.group(2).trim();
                Option option = new Option();
                option.setText(text);
                option.setExercise(exercise);
                options.add(option);
            }
        }

        if (exercise.getQuestion() == null)
            throw new ExerciseParseException("Missing question for exercise " + exerciseCode);
        if (exercise.getType() == null)
            throw new ExerciseParseException("Missing type for exercise " + exerciseCode);

        if ("SHORT_ANSWER".equals(exercise.getType())) {
            if (correctAnswer == null || correctAnswer.isEmpty()) {
                System.out.println(correctAnswer);
                throw new ExerciseParseException("Missing solution for SHORT_ANSWER exercise " + exerciseCode);
            }
            exercise.setAnswer(correctAnswer);
            exercise.setOptions(null);
        } else {
            if (!options.isEmpty()) {
                if (correctAnswer == null || correctAnswer.isEmpty()) {
                    throw new ExerciseParseException("Missing solution for exercise " + exerciseCode);
                }
                setCorrectAnswers(options, correctAnswer, exercise.getType());
            }
            exercise.setOptions(options);
        }

        exercise.setOptions(options);
        return exercise;
    }

    private void setCorrectAnswers(List<Option> options, String correctAnswer, String type) {
        switch (type) {
            case "MULTIPLE_CHOICE" -> {
                Set<String> correctSet = new HashSet<>(Arrays.asList(correctAnswer.split(",\\s*")));
                for (int i = 0; i < options.size(); i++) {
                    String label = String.valueOf((char) ('A' + i));
                    options.get(i).setCorrect(correctSet.contains(label));
                }
            }
            case "TRUE_FALSE" -> options.forEach(opt -> opt.setCorrect(
                    opt.getText().equalsIgnoreCase(correctAnswer)));
            case "SHORT_ANSWER" -> {
                if (!options.isEmpty()) {
                    options.get(0).setCorrect(true);
                }
            }
        }
    }

    private String mapExerciseType(String type) {
        return switch (type.toUpperCase()) {
            case "MULTIPLE CHOICE" -> "MULTIPLE_CHOICE";
            case "TRUE/FALSE" -> "TRUE_FALSE";
            case "SHORT ANSWER" -> "SHORT_ANSWER";
            case "ESSAY" -> "ESSAY";
            case "FILL IN THE BLANK" -> "FILL_IN_THE_BLANK";
            default -> type.toUpperCase();
        };
    }

    public static class ImportResult {
        private int coursesProcessed = 0;
        private int topicsProcessed = 0;
        private int lessonsProcessed = 0;
        private int exercisesProcessed = 0;
        private final List<String> errors = new ArrayList<>();

        public void incrementCoursesProcessed() {
            coursesProcessed++;
        }

        public void incrementTopicsProcessed() {
            topicsProcessed++;
        }

        public void incrementLessonsProcessed() {
            lessonsProcessed++;
        }

        public void incrementExercisesProcessed() {
            exercisesProcessed++;
        }

        public void addError(String error) {
            errors.add(error);
        }

        public int getCoursesProcessed() {
            return coursesProcessed;
        }

        public int getTopicsProcessed() {
            return topicsProcessed;
        }

        public int getLessonsProcessed() {
            return lessonsProcessed;
        }

        public int getExercisesProcessed() {
            return exercisesProcessed;
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }

    private static class ExerciseParseException extends Exception {
        public ExerciseParseException(String message) {
            super(message);
        }
    }
}
