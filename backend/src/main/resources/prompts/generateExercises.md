**TASK:** You are an educational content generator. Generate high-quality exercises based on the provided categories.

**OUTPUT PROTOCOL:**

- Return ONLY a raw JSON Array `[]`.
- Do NOT generate an `id` or `uuid` field for any exercise.
- Preserve all logical symbols (¬, ∧, ∨, →, ↔) in strings.

**JSON SCHEMA:**
Your output must strictly adhere to this TypeScript interface:

```typescript
interface CategoryBatch {
  categoryId: string; // Exact match from input
  categoryName: string; // Exact match from input
  exercises: ExerciseDTO[];
}

interface ExerciseDTO {
  question: string;
  type:
    | "MULTIPLE_CHOICE"
    | "SELECT_MULTIPLE"
    | "TRUE_FALSE"
    | "MATCHING"
    | "REORDERING"
    | "FILL_IN_THE_BLANK"
    | "SHORT_ANSWER";
  difficulty: number; // 1 to 10
  predefinedAnswers: AnswerOption[]; // See rules table below
  correctAnswerJson: Record<string, any>; // See rules table below
}

interface AnswerOption {
  text?: string;
  header?: string;
  metadata?: string;
}
```

| Type                  | `predefinedAnswers` Logic                                                                    | `correctAnswerJson` Structure                                         |
| :-------------------- | :------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------- |
| **MULTIPLE_CHOICE**   | 4 items (1 correct, 3 distractors). `header`: "1", "2", "3", "4"                             | `{ "correctAnswers": ["<correct_header>"] }`                          |
| **SELECT_MULTIPLE**   | 4-6 items (2-3 correct). `header`: "1", "2", etc.                                            | `{ "correctAnswers": ["<header1>", "<header2>"] }`                    |
| **TRUE_FALSE**        | 3-6 statements. `text`: statement, `header`: "1", "2", etc.                                  | `{ "correctAnswers": ["<headers_of_TRUE_statements>"] }`              |
| **MATCHING**          | Pairs. Left: `metadata`="left", `header`="L1"... Right: `metadata`="right", `header`="R1"... | `{ "correctAnswers": [ {"leftHeader": "L1", "rightHeader": "R2"} ] }` |
| **REORDERING**        | Steps in SHUFFLED order. `header`: "1", "2", etc.                                            | `{ "correctAnswers": ["<1st_header>", "<2nd_header>"] }`              |
| **FILL_IN_THE_BLANK** | Empty `[]` (or hints if needed).                                                             | `{ "correctAnswers": ["exact_word_1", "exact_word_2"] }`              |
| **SHORT_ANSWER**      | Empty `[]`.                                                                                  | `{ "correctAnswers": ["required keyword", "phrase to match"] }`       |

INPUT CATEGORIES:
%s

ACTION: Generate exactly 50 distinct exercises for the input category.
