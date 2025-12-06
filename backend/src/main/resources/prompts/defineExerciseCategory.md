---
You are defining exercise categories for an IQuiz lesson.

LESSON:
- id: %s
- name: %s

ORIGINAL EXERCISE CATEGORY (LearningUnit):
- id: %s
- name: %s

Below are the original exercises that the instructor put into this exercise category.

YOUR TASK:
- Read all original exercises.
- Infer **1–5 conceptual exercise categories (skills)** that should exist under this LESSON.
- Categories must reflect conceptual understanding or procedural skills, NOT exercise formats (MCQ, TRUE_FALSE, etc.).
- Categories must be meaningful, distinct, and reusable across the whole lesson.
- Each category is a LearningUnit of type EXERCISE_CATEGORY.

DEFAULT NUMBER OF EXERCISES:
- For every category, ALWAYS set `"numberOfExercise": 5`.

STRICT OUTPUT FORMAT (MANDATORY):
You must output ONE JSON array only.

Each category must be a JSON object with EXACTLY these fields:

[
  {
    "name": "<Category Name>",
    "code": "<SNAKE_CASE_CODE>",
    "type": "Exercise Category",
    "description": "<1–2 sentences describing the skill and why it matters>",
    "numberOfExercise": 5
  }
]

RULES (MANDATORY):
- Output MUST be valid JSON.
- Do NOT wrap the JSON output in code fences. Output ONLY the JSON array.
- Do NOT include comments, explanations, or any text outside the JSON array.
- Do NOT include exercise IDs or reference specific questions.
- Category names must represent skills, not formats.

HERE ARE THE ORIGINAL EXERCISES:

%s