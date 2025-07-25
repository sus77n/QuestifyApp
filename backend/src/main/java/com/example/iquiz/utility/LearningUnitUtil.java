package com.example.iquiz.utility;

import com.example.iquiz.entity.LearningUnit;

public class LearningUnitUtil {

    public static long countExercises(LearningUnit unit) {
        long numberOfExercises = 0;

        if (unit.getExercises() != null && unit.getExercises().size() > 0) {
            return unit.getExercises().size();
        }

        for (LearningUnit lu : unit.getChildren()) {
            numberOfExercises += countExercises(lu);
        }

        return numberOfExercises;
    }

}
