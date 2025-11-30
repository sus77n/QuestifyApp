import React from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import MultipleChoiceExercise from "../ExerciseTypes/MultipleChoiceExercise";
import ShortAnswerExercise from "../ExerciseTypes/ShortAnswerExercise";
import FillInBlankExercise from "../ExerciseTypes/FillInBlankExercise";
import SelectMultipleExercise from "../ExerciseTypes/SelectMultipleExercise";
import MatchingExercise from "../ExerciseTypes/MatchingExercise";
import ReorderingExercise from "../ExerciseTypes/ReorderingExercise";
import TrueFalseExercise from "../ExerciseTypes/TrueFalseExercise";


export default function ExerciseRenderer({
                                             exercise,
                                             submission,
                                             onSubmissionChange,
                                         }: {
    exercise: ExerciseDTO;
    submission: any;
    onSubmissionChange: (s: any) => void;
}) {
    if (!exercise) return <p>No exercise selected.</p>;

    switch (exercise.type) {
        case "MULTIPLE_CHOICE":

            return (
                <MultipleChoiceExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "SHORT_ANSWER":

            return (
                <ShortAnswerExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "FILL_IN_THE_BLANK":
            return (
                <FillInBlankExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "SELECT_MULTIPLE":

            return (
                <SelectMultipleExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "MATCHING":
            return (
                <MatchingExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "REORDERING":
            return (
                <ReorderingExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        case "TRUE_FALSE":
            return (
                <TrueFalseExercise
                    exercise={exercise}
                    submission={submission}
                    onSubmissionChange={onSubmissionChange}
                />
            );

        default:
            return (
                <div className="text-red-600 p-6">
                    Unsupported exercise type: <b>{exercise.type}</b>
                </div>
            );
    }
}
