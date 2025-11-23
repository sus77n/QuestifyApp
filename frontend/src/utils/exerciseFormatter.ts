import {ExerciseDTO} from "../model/ExerciseDTO";

type AnswerData =
    | number[]
    | string[]
    | { leftId: number; rightId: number }[];

export function formatExerciseForSubmit(
    exercise: Omit<ExerciseDTO, "correctAnswers"> & { parsedCorrectAnswers: AnswerData }
): ExerciseDTO {
    return {
        ...exercise,
        correctAnswers: JSON.stringify(exercise.parsedCorrectAnswers),
    };
}


export type ParsedExercise =
    | (ExerciseDTO & {
    parsedCorrectAnswers: number[];
})
    | (ExerciseDTO & {
    parsedCorrectAnswers: string[];
})
    | (ExerciseDTO & {
    parsedCorrectAnswers: { leftId: number; rightId: number }[];
});

export function parseExercise(exercise: ExerciseDTO): ParsedExercise {
    let parsed: any;
    try {
        parsed = JSON.parse(exercise.correctAnswers);
    } catch {
        parsed = [];
    }

    switch (exercise.type) {
        case "MULTIPLE_CHOICE":
        case "SELECT_MULTIPLE":
        case "TRUE_FALSE":
            return { ...exercise, parsedCorrectAnswers: parsed as number[] };
        case "SHORT_ANSWER":
        case "FILL_IN_THE_BLANK":
        case "REORDERING":
            return { ...exercise, parsedCorrectAnswers: parsed as string[] };
        case "MATCHING":
            return {
                ...exercise,
                parsedCorrectAnswers: parsed as { leftId: number; rightId: number }[],
            };
        default:
            return { ...exercise, parsedCorrectAnswers: [] };
    }
}
