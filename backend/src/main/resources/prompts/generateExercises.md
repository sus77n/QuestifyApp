TASK:
You are an educational content generator. Generate high-quality exercises based on the provided categories.

OUTPUT PROTOCOL (STRICT):
- Return ONLY a raw JSON array [].
- Do NOT include explanations, markdown, or comments.
- Do NOT wrap the JSON in ```json.
- Do NOT generate id or uuid fields.
- Ensure the JSON is valid and parsable.
- Do NOT include trailing commas.
- Preserve all logical symbols (¬, ∧, ∨, →, ↔) inside strings.

JSON SCHEMA (STRICT):
interface CategoryBatch {
categoryId: string;
categoryName: string;
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
difficulty: number; // 1–10
predefinedAnswers: AnswerOption[];
correctAnswerJson: Record<string, any>;
}

interface AnswerOption {
text?: string;
header?: string;
metadata?: Record<string, string>;
}

EXERCISE RULES TABLE:

MULTIPLE_CHOICE
- Exactly 4 options
- One correct answer
- Headers must be "1","2","3","4"
- correctAnswerJson = { "correctAnswers": ["<correct_header>"] }

SELECT_MULTIPLE
- 4–6 options
- 2–3 correct answers
- correctAnswerJson = { "correctAnswers": ["1","3"] }

TRUE_FALSE
- 3–6 statements
- text = statement
- header = "1","2","3"...
- correctAnswerJson = { "correctAnswers": ["headers_of_true_statements"] }

MATCHING
- Equal number of LEFT and RIGHT items
- LEFT items: metadata = { "side": "left" }, header = "L1","L2","L3"
- RIGHT items: metadata = { "side": "right" }, header = "R1","R2","R3"
- Order must be shuffled
- correctAnswerJson =
  { "correctAnswers": [ { "leftHeader": "L1", "rightHeader": "R2" } ] }

REORDERING
- Steps must be in shuffled order
- header = "1","2","3","4"
- correctAnswerJson must list headers in correct order

FILL_IN_THE_BLANK
- predefinedAnswers = []
- correctAnswerJson = { "correctAnswers": ["exact_word"] }

SHORT_ANSWER
- predefinedAnswers = []
- correctAnswerJson = { "correctAnswers": ["keyword"] }

GENERATION RULES:
- Generate a balanced mix of all exercise types.
- Difficulty should vary between 1 and 10.
- Questions must be clear and unambiguous.
- Avoid duplicate questions.
- Avoid duplicate answer texts.
- Ensure headers referenced in correctAnswerJson exist in predefinedAnswers.

INPUT CATEGORIES:
%s

ACTION:
Generate exercises for this category.
Return the JSON array only.