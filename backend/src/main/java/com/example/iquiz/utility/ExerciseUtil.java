package com.example.iquiz.utility;

import com.example.iquiz.dto.CatInfo;
import com.example.iquiz.entity.*;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExerciseUtil {

    private final ExerciseRepository exerciseRepository;
    private final AttemptDetailRepository attemptDetailRepository;
    private final LessonConfigRepository lessonConfigRepository;
    private final UserMasteryRepository userMasteryRepository;

    public List<Exercise> selectExercises(UUID userId, LearningUnit lesson) {

        LessonConfig config = lessonConfigRepository.findByLessonId(lesson.getId())
                .orElseThrow(() -> new ResourceNotFoundException("LessonConfig", "lessonId", lesson.getId()));

        if (config.getQuestionsPerAttempt() <= 0) {
            return List.of();
        }

        List<Exercise> candidatePool =
                buildCandidatePool(userId, lesson, config);

        Map<UUID, List<Exercise>> poolByCategory =
                candidatePool.stream()
                        .collect(Collectors.groupingBy(ex -> ex.getParent().getId()));

        List<CatInfo> catInfos =
                buildCategoryInfos(userId, lesson, poolByCategory);

        allocateQuestions(catInfos, config.getQuestionsPerAttempt(), poolByCategory);

        return pickExercises(catInfos, poolByCategory, config.getQuestionsPerAttempt());
    }

    public List<Exercise> buildCandidatePool(UUID userId, LearningUnit lesson, LessonConfig config) {

        List<UUID> categoryIds = lesson.getChildren().stream()
                .map(LearningUnit::getId)
                .toList();

        List<Exercise> pool = exerciseRepository.findByParent_IdIn(categoryIds);

        if (!config.isNoRepeatScope()) return pool;

        Set<UUID> used = new HashSet<>(
                attemptDetailRepository.findUsedExerciseIdsByUserAndLesson(userId, lesson.getId())
        );

        List<Exercise> filtered = pool.stream()
                .filter(ex -> !used.contains(ex.getId()))
                .toList();

        // fallback if not enough
        if (filtered.size() < config.getQuestionsPerAttempt()) {
            return pool;
        }

        return filtered;
    }

    public List<CatInfo> buildCategoryInfos(
            UUID userId,
            LearningUnit lesson,
            Map<UUID, List<Exercise>> poolByCategory
    ) {

        Set<UUID> categoryIds = poolByCategory.keySet();

        List<UserMastery> masteryList =
                userMasteryRepository.findByUserIdAndLessonId(userId, lesson.getId());

        Map<UUID, UserMastery> masteryByCategory = masteryList.stream()
                .collect(Collectors.toMap(
                        m -> m.getId().getExerciseCategoryId(),
                        Function.identity(),
                        (m1, m2) -> m1
                ));

        List<CatInfo> catInfos = new ArrayList<>();

        for (UUID catId : categoryIds) {

            UserMastery mastery = masteryByCategory.get(catId);
            double accuracy = (mastery != null) ? mastery.getAccuracy() : 0.5; // default 50%

            CatInfo info = new CatInfo();
            info.setCategoryId(catId);
            info.setBaseWeight(1.0);
            info.setMin(null);
            info.setMax(null);
            info.setAccuracy(accuracy);
            info.setWeight((1.0 - accuracy) * 1.0);
            catInfos.add(info);
        }


        return catInfos;
    }

    private void allocateQuestions(
            List<CatInfo> catInfos,
            int totalQuestions,
            Map<UUID, List<Exercise>> poolByCategory
    ) {
        double totalWeight = catInfos.stream().mapToDouble(ci -> ci.getWeight()).sum();
        if (totalWeight <= 0) {
            catInfos.forEach(ci -> ci.setWeight(ci.getBaseWeight()));
            totalWeight = catInfos.stream().mapToDouble(ci -> ci.getWeight()).sum();
        }

        // 10. Compute rate & initial integer counts
        for (CatInfo ci : catInfos) {
            ci.setRate(ci.getWeight() / totalWeight);
            double raw = totalQuestions * ci.getRate();
            int floor = (int) Math.floor(raw);
            ci.setTarget(floor);
            ci.setFrac(raw - floor);
        }

        // 11. Apply min/max and cap by available pool size
        for (CatInfo ci : catInfos) {
            if (ci.getMin() != null) {
                ci.setTarget(Math.max(ci.getTarget(), ci.getMin().intValue()));
            }
            if (ci.getMax() != null) {
                ci.setTarget(Math.min(ci.getTarget(), ci.getMax().intValue()));
            }

            int available = poolByCategory.get(ci.getCategoryId()).size();
            if (ci.getTarget() > available) {
                ci.setTarget(available);
            }
        }

        // 12. Rebalance so sum(getTarget()) ~= totalQuestions
        int currentTotal = catInfos.stream().mapToInt(ci -> ci.getTarget()).sum();

        // 12.a Add if we are short
        while (currentTotal < totalQuestions) {
            CatInfo best = catInfos.stream()
                    .filter(ci -> {
                        int available = poolByCategory.get(ci.getCategoryId()).size();
                        return ((ci.getMax() == null || ci.getTarget() < ci.getMax())
                                && ci.getTarget() < available);
                    })
                    .max(Comparator.comparingDouble(ci -> ci.getFrac()))
                    .orElse(null);

            if (best == null) break;
            best.setTarget(best.getTarget() + 1);
            currentTotal++;
        }

        // 12.b Remove if we exceed
        while (currentTotal > totalQuestions) {
            CatInfo worst = catInfos.stream()
                    .filter(ci -> ci.getMin() == null || ci.getTarget() > ci.getMin())
                    .min(Comparator.comparingDouble(ci -> ci.getFrac()))
                    .orElse(null);

            if (worst == null) break;
            worst.setTarget(worst.getTarget() - 1);
            currentTotal--;
        }

    }

    private List<Exercise> pickExercises(
            List<CatInfo> cats,
            Map<UUID, List<Exercise>> poolByCategory,
            int totalQuestions
    ) {
        List<Exercise> result = new ArrayList<>();
        Random random = new Random();

        for (CatInfo ci : cats) {
            List<Exercise> pool = new ArrayList<>(poolByCategory.get(ci.getCategoryId()));
            Collections.shuffle(pool, random);

            result.addAll(pool.subList(0, Math.min(ci.getTarget(), pool.size())));
        }

        // fallback fill
        if (result.size() < totalQuestions) {
            List<Exercise> leftovers = new ArrayList<>();
            poolByCategory.values().forEach(leftovers::addAll);

            leftovers.removeAll(result);
            Collections.shuffle(leftovers, random);

            result.addAll(leftovers.subList(0,
                    Math.min(totalQuestions - result.size(), leftovers.size())));
        }

        return result;
    }

}
