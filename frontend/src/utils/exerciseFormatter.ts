import {ExerciseDTO} from "../model/ExerciseDTO";


export type ParsedExercise =
    | (ExerciseDTO & {
    parsedCorrectAnswers: string[];
})
    | (ExerciseDTO & {
    parsedCorrectAnswers: { leftId: string; rightId: string }[];
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
            return { ...exercise, parsedCorrectAnswers: parsed as string[] };
        case "SHORT_ANSWER":
        case "FILL_IN_THE_BLANK":
        case "REORDERING":
            return { ...exercise, parsedCorrectAnswers: parsed as string[] };
        case "MATCHING":
            return {
                ...exercise,
                parsedCorrectAnswers: parsed as { leftId: string; rightId: string }[],
            };
        default:
            return { ...exercise, parsedCorrectAnswers: [] };
    }
}
