package com.example.iquiz.utility;

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
public class MarkdownReaderUtil {

    private static final Logger logger = LoggerFactory.getLogger(MarkdownReaderUtil.class);
    private static String BASE_DICTIONARY = "src/main/resources/generation/discreteMath1/questions/generated/";

    private static final Pattern HEADER_PATTERN = Pattern.compile("^## #Course #([^#]+) #([^#]+)$|^## #Topic #([^#]+)$|^## #Lesson #([^#]+)$");
    private static final Pattern EXERCISE_HEADER_PATTERN = Pattern.compile("^## EX\\s+S\\d+\\.\\d+$");
    private static final Pattern TYPE_PATTERN = Pattern.compile("^\\*\\*ResponseType\\*\\*: (.+)$");
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\*\\*Question\\*\\*: (.+)$");
    private static final Pattern OPTIONS_START_PATTERN = Pattern.compile("^\\*\\*Options\\*\\*:$");
    private static final Pattern OPTION_PATTERN = Pattern.compile("^- ([A-Z])\\. (.+)$");
    private static final Pattern SOLUTION_PATTERN = Pattern.compile("^\\*\\*Solution\\*\\*: (.+)$");
    private static final Pattern DIFFICULTY_PATTERN = Pattern.compile("^\\*\\*Difficulty\\*\\*: (\\d+)$");

    private final LearningUnitRepository learningUnitRepository;
    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final ExerciseRepository exerciseRepository;
    private final OptionRepository optionRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final UserRepository userRepository;

    public MarkdownReaderUtil(LearningUnitRepository learningUnitRepository,
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
        try (BufferedReader reader = new BufferedReader(new FileReader(BASE_DICTIONARY + markdownPath))) {
            LearningUnit currentCourse = null;
            LearningUnit currentTopic = null;
            LearningUnit currentLesson = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Matcher headerMatcher = HEADER_PATTERN.matcher(line);
                if (headerMatcher.matches()) {
                    if (headerMatcher.group(1) != null) {
                        String courseTitle = headerMatcher.group(1).trim();
                        String courseCode = headerMatcher.group(2).trim();
                        currentCourse = findOrCreateLearningUnit(courseTitle, "COURSE", null, creator, courseCode);
                        currentTopic = null;
                        currentLesson = null;
                        result.incrementCoursesProcessed();
                    } else if (headerMatcher.group(3) != null) { // Topic
                        if (currentCourse == null)
                            throw new IllegalStateException("Topic defined without a Course context");
                        String topicTitle = headerMatcher.group(3).trim();
                        currentTopic = findOrCreateLearningUnit(topicTitle, "TOPIC", currentCourse, creator, null);
                        currentLesson = null;
                        result.incrementTopicsProcessed();
                    } else if (headerMatcher.group(4) != null) { // Lesson
                        if (currentTopic == null)
                            throw new IllegalStateException("Lesson defined without a Topic context");
                        String lessonTitle = headerMatcher.group(4).trim();
                        currentLesson = findOrCreateLearningUnit(lessonTitle, "LESSON", currentTopic, creator, null);
                        result.incrementLessonsProcessed();
                    }
                    continue;
                }

                Matcher exerciseHeaderMatcher = EXERCISE_HEADER_PATTERN.matcher(line);
                if (exerciseHeaderMatcher.matches() && currentLesson != null) {
                    try {
                        Exercise exercise = parseExercise(reader);
                        exercise.setParent(currentLesson);
                        exerciseRepository.save(exercise);
                        result.incrementExercisesProcessed();
                    } catch (ExerciseParseException e) {
                        logger.error("Failed to parse exercise: {}", e.getMessage());
                        result.addError(e.getMessage());
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

    private Exercise parseExercise(BufferedReader reader) throws IOException, ExerciseParseException {
        Exercise exercise = new Exercise();
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now());
        List<Option> options = new ArrayList<>();
        String line;
        String correctAnswer = null;
        boolean optionsSection = false;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.equals("---")) break;

            Matcher typeMatcher = TYPE_PATTERN.matcher(line);
            if (typeMatcher.matches()) {
                exercise.setType(mapExerciseType(typeMatcher.group(1).trim()));
                continue;
            }
            Matcher questionMatcher = QUESTION_PATTERN.matcher(line);
            if (questionMatcher.matches()) {
                exercise.setQuestion(questionMatcher.group(1).trim());
                continue;
            }
            Matcher optionsStartMatcher = OPTIONS_START_PATTERN.matcher(line);
            if (optionsStartMatcher.matches()) {
                optionsSection = true;
                continue;
            }
            if (optionsSection) {
                Matcher optionMatcher = OPTION_PATTERN.matcher(line);
                if (optionMatcher.matches()) {
                    String label = optionMatcher.group(1).trim();
                    String text = optionMatcher.group(2).trim();
                    Option option = new Option();
                    option.setText(text);
                    option.setExercise(exercise);
                    options.add(option);
                    continue;
                } else {
                    optionsSection = false;
                }
            }
            Matcher solutionMatcher = SOLUTION_PATTERN.matcher(line);
            if (solutionMatcher.matches()) {
                correctAnswer = solutionMatcher.group(1).trim();
                continue;
            }
            Matcher difficultyMatcher = DIFFICULTY_PATTERN.matcher(line);
            if (difficultyMatcher.matches()) {
                exercise.setDifficulty(Integer.parseInt(difficultyMatcher.group(1)));
                continue;
            }
        }
        if (exercise.getQuestion() == null)
            throw new ExerciseParseException("Missing question for exercise");
        if (exercise.getType() == null)
            throw new ExerciseParseException("Missing type for exercise");
        if ("SHORT_ANSWER".equals(exercise.getType())) {
            if (correctAnswer == null || correctAnswer.isEmpty()) {
                throw new ExerciseParseException("Missing solution for SHORT_ANSWER exercise");
            }
            exercise.setAnswer(correctAnswer);
            exercise.setOptions(null);
        } else {
            if (!options.isEmpty()) {
                if (correctAnswer == null || correctAnswer.isEmpty()) {
                    throw new ExerciseParseException("Missing solution for exercise");
                }
                setCorrectAnswers(options, correctAnswer, exercise.getType());
            }
            exercise.setOptions(options);
        }
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
