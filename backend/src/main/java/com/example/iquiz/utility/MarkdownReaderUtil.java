package com.example.iquiz.utility;

import com.example.iquiz.entity.*;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ApiException;
import com.example.iquiz.exception.ErrorCode;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MarkdownReaderUtil {

    private static final Logger logger = LoggerFactory.getLogger(MarkdownReaderUtil.class);
    private static final String BASE_DICTIONARY =
            "src/main/resources/generation/discreteMath1/questions/generated/";

    // ====== HEADER PATTERNS ======
    private static final Pattern COURSE_PATTERN =
            Pattern.compile("^##\\s+#Course\\s+#(.+)\\s+#(.+)$");
    private static final Pattern TOPIC_PATTERN =
            Pattern.compile("^##\\s+#Topic\\s+#(.+)\\s+#(.+)$");
    private static final Pattern LESSON_PATTERN =
            Pattern.compile("^##\\s+#Lesson\\s+#(.+)\\s+#(.+)$");
    private static final Pattern EXERCISE_CATEGORY_PATTERN =
            Pattern.compile("^##\\s+#Exercise Category\\s+#(.+)\\s+#(.+)$");


    // ====== EXERCISE PATTERNS ======
    private static final Pattern EX_PATTERN = Pattern.compile("^###\\s+EX\\s+.+$");

    private static final Pattern DIFFICULTY_PATTERN = Pattern.compile("^\\*\\*Difficulty\\*\\*: (\\d+)$");
    private static final Pattern TYPE_PATTERN = Pattern.compile("^\\*\\*ExerciseType\\*\\*: (.+)$");
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\*\\*Question\\*\\*: (.+)$");

    // ====== OPTION BLOCK PATTERNS ======
    private static final Pattern OPTIONS_PATTERN = Pattern.compile("^\\*\\*Options\\*\\*:$");
    private static final Pattern OPTIONS_LEFT_PATTERN = Pattern.compile("^\\*\\*OptionsLeft\\*\\*:$");
    private static final Pattern OPTIONS_RIGHT_PATTERN = Pattern.compile("^\\*\\*OptionsRight\\*\\*:$");

    private static final Pattern OPTION_LINE_PATTERN = Pattern.compile("^-\\s+(\\d+)\\.\\s+(.+)$");

    // ====== ANSWER JSON PATTERN ======
    private static final Pattern ANSWER_JSON_PATTERN = Pattern.compile("^\\*\\*Answer\\*\\*: (\\{.*}$)");

    // ====== REPOSITORIES ======
    private final LearningUnitRepository learningUnitRepository;
    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final ExerciseRepository exerciseRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final LessonConfigRepository lessonConfigRepository;

    public MarkdownReaderUtil(
            LearningUnitRepository learningUnitRepository,
            LearningUnitTypeRepository learningUnitTypeRepository,
            ExerciseRepository exerciseRepository,
            AnswerRepository answerRepository,
            UserRepository userRepository,
            LessonConfigRepository lessonConfigRepository) {
        this.learningUnitRepository = learningUnitRepository;
        this.learningUnitTypeRepository = learningUnitTypeRepository;
        this.exerciseRepository = exerciseRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.lessonConfigRepository = lessonConfigRepository;
    }

    @Transactional
    public void parseAndSaveMarkdown(String markdownPath, UUID creatorId) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId));

        try (BufferedReader reader = new BufferedReader(new FileReader(BASE_DICTIONARY + markdownPath))) {

            LearningUnit course = null;
            LearningUnit topic = null;
            LearningUnit lesson = null;
            LearningUnit exCate = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // ====== COURSE ======
                Matcher mc = COURSE_PATTERN.matcher(line);
                if (mc.matches()) {
                    course = findOrCreateLearningUnit(mc.group(1).trim(), "Course", null, creator, mc.group(2).trim());
                    topic = null;
                    lesson = null;
                    exCate = null;
                    continue;
                }

                // ====== TOPIC ======
                Matcher mt = TOPIC_PATTERN.matcher(line);
                if (mt.matches()) {
                    topic = findOrCreateLearningUnit(mt.group(1).trim(), "Topic", course, creator, mt.group(2).trim());
                    lesson = null;
                    continue;
                }

                // ====== LESSON ======
                Matcher ml = LESSON_PATTERN.matcher(line);
                if (ml.matches()) {
                    lesson = findOrCreateLearningUnit(ml.group(1).trim(), "Lesson", topic, creator, ml.group(2).trim());

                    // ====== Lesson Config ======
                    LessonConfig config = lessonConfigRepository.findByLessonId(lesson.getId()).orElse(null);
                    if (config == null) {
                        config = new LessonConfig();
                        config.setLesson(lesson);
                        config.setQuestionsPerAttempt(10);
                        config.setPassThreshold(70);
                        config.setNoRepeatScope(true);
                        lessonConfigRepository.save(config);
                    }

                    continue;
                }

                // ====== Exercise Category ======
                Matcher mec = EXERCISE_CATEGORY_PATTERN.matcher(line);
                if (mec.matches()) {
                    exCate = findOrCreateLearningUnit(mec.group(1).trim(), "Exercise Category", lesson, creator, mec.group(2).trim());

                    continue;
                }

                // ====== EXERCISE BLOCK ======
                if (EX_PATTERN.matcher(line).matches() && lesson != null) {
                    Exercise ex = parseExercise(reader);
                    ex.setParent(exCate);

                    exerciseRepository.save(ex);
                }

            }

        } catch (IOException e) {
            throw new ApiException("Error reading markdown file", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // =====================================================================
    //                         EXERCISE PARSER
    // =====================================================================
    private Exercise parseExercise(BufferedReader reader) throws IOException {
        Exercise ex = new Exercise();
        ex.setCreatedAt(LocalDateTime.now());
        ex.setUpdatedAt(LocalDateTime.now());

        List<Answer> options = new ArrayList<>();

        boolean inLeft = false;
        boolean inRight = false;
        boolean inOptions = false;

        String line;

        // mark once at start
        reader.mark(10000);

        while ((line = reader.readLine()) != null) {

            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // If next exercise starts, exit now
            if (trimmed.startsWith("### EX")) {
                reader.reset();  // go back so outer loop sees this EX header
                break;
            }

            reader.mark(10000);

            Matcher md = DIFFICULTY_PATTERN.matcher(trimmed);
            if (md.matches()) {
                ex.setDifficulty(Integer.parseInt(md.group(1)));
                continue;
            }

            Matcher mt = TYPE_PATTERN.matcher(trimmed);
            if (mt.matches()) {
                ex.setType(ExerciseType.valueOf(mt.group(1).trim().toUpperCase()));
                continue;
            }

            Matcher mq = QUESTION_PATTERN.matcher(trimmed);
            if (mq.matches()) {
                ex.setQuestion(mq.group(1).trim());
                continue;
            }

            if (OPTIONS_PATTERN.matcher(trimmed).matches()) {
                inOptions = true;
                inLeft = false;
                inRight = false;
                continue;
            }

            if (OPTIONS_LEFT_PATTERN.matcher(trimmed).matches()) {
                inLeft = true;
                inRight = false;
                inOptions = false;
                continue;
            }

            if (OPTIONS_RIGHT_PATTERN.matcher(trimmed).matches()) {
                inLeft = false;
                inRight = true;
                inOptions = false;
                continue;
            }

            Matcher mo = OPTION_LINE_PATTERN.matcher(trimmed);
            if (mo.matches()) {
                Answer ans = new Answer();
                ans.setHeader(mo.group(1).trim());
                ans.setText(mo.group(2).trim());

                if (inLeft) ans.setMetadata("{\"side\":\"left\"}");
                else if (inRight) ans.setMetadata("{\"side\":\"right\"}");
                else ans.setMetadata(null);

                options.add(ans);
                continue;
            }

            if (trimmed.startsWith("**Answer**:")) {

                if (trimmed.contains("{") && trimmed.endsWith("}")) {
                    String json = trimmed.substring(trimmed.indexOf("{")).trim();
                    ex.setCorrectAnswerJson(json);
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                sb.append(trimmed.substring(trimmed.indexOf("{")).trim());

                while ((line = reader.readLine()) != null) {
                    String t = line.trim();
                    sb.append(t);
                    if (t.endsWith("}")) break;
                }

                ex.setCorrectAnswerJson(sb.toString());
            }
        }

        Exercise saved = exerciseRepository.save(ex);

        for (Answer a : options) {
            a.setExercise(saved);
            answerRepository.save(a);
        }

        saved.setPredefinedAnswers(options);

        return exerciseRepository.save(saved);
    }



    // =====================================================================
    //                   LEARNING UNIT HELPER
    // =====================================================================
    private LearningUnit findOrCreateLearningUnit(
            String name, String typeName, LearningUnit parent, User creator, String code) {

        LearningUnitType type = learningUnitTypeRepository.findByName(typeName).orElse(null);

        if (type == null) {
            type = new LearningUnitType();
            type.setName(typeName);
            type.setLevel(getLevelForType(typeName));
            type = learningUnitTypeRepository.save(type);
        }

        LearningUnitType finalType = type;

        return learningUnitRepository.findByNameAndTypeId(name, finalType.getId())
                .orElseGet(() -> {
                    LearningUnit u = new LearningUnit();
                    u.setName(name);
                    u.setType(finalType);
                    u.setParent(parent);
                    u.setCreatedBy(creator);
                    u.setCode(code);
                    return learningUnitRepository.save(u);
                });
    }

    private int getLevelForType(String typeName) {
        return switch (typeName) {
            case "Course" -> 1;
            case "Topic" -> 2;
            case "Lesson" -> 3;
            case "Exercise Category" -> 4;
            default -> 0;
        };
    }

}
