## Task
Generate a new practice question file based on the content in  
**/questions/original/*.md**  
and save the result as a new file named  
**/questions/generated/*.md**

## Format for Header
- ## #Course #[Course Title] #[Course Code]
- ## #Topic #[Topic Title] #[Topic Code]
- ## #Lesson #[Lesson Title] #[Lesson Code]
- ## #Exercise Category #[Exercise Category Title] #[Exercise Category Code]

All codes must follow the hierarchical pattern:

Course Code → C{number}

Chapter/Topic Code → C{courseNumber}-CH{chapterNumber}

Lesson Code → C{courseNumber}-CH{chapterNumber}-L{lessonNumber}

Exercise Category Code →
C{courseNumber}-CH{chapterNumber}-L{lessonNumber}-EC{exerciseCategoryNumber}

Example:
Course 1 → C1
Chapter 1 → C1-CH1
Lesson 1 → C1-CH1-L1
Exercise Category 1 → C1-CH1-L1-EC1

## Requirements
Generate exactly 70 questions per lesson:
- 10 MULTIPLE_CHOICE
- 10 SHORT_ANSWER
- 10 MATCHING
- 10 REORDERING
- 10 FILL_IN_THE_BLANK
- 10 SELECT_MULTIPLE
- 10 TRUE_FALSE

## Format for Questions
EX <lesson_id>.<question_number>
**Difficulty**: <1–10>
**ExerciseType**: <type>
**Question**: <main question text>

---

## Options (for MULTIPLE_CHOICE, SELECT_MULTIPLE, TRUE_FALSE)

### MULTIPLE_CHOICE
- MUST generate exactly 4 options
- 1 correct + 3 distractors
- Distractors must be logically plausible but incorrect

### SELECT_MULTIPLE
- Must generate 4–6 options
- 2–3 correct answers
- Distractors should be partially correct or common mistakes

### TRUE_FALSE (Multiple Statements)
TRUE_FALSE questions behave like multi-statement evaluations.

Format:
**Question**: True or False
**Options**:
- 1. <statement>
- 2. <statement>
- 3. <statement>

**Answer**: { "correctAnswers": [IDs of TRUE statements] }

Rules:
- Generate 3–6 statements
- Each statement is treated like an option
- Only TRUE statements go to correctAnswers
- FALSE statements are omitted

---

## MATCHING Format (Option A)
**OptionsLeft**:
- 1. <left text>
- 2. <left text>
- 3. <left text>

**OptionsRight**:
- 1. <right text>
- 2. <right text>
- 3. <right text>

**Answer**: {
"correctAnswers": [
{"leftHeader": 1, "rightHeader": 2},
{"leftHeader": 2, "rightHeader": 3},
{"leftHeader": 3, "rightHeader": 1}
]
}

---

## REORDERING Format
**Options**:
- 1. <step>
- 2. <step>
- 3. <step>

**Answer**: { "correctAnswers": ["1", "3", "2"] }

---

## FILL_IN_THE_BLANK Format (Multi-Blanks)
**Answer**: { "correctAnswers": ["blank1", "blank2", "blank3"] }

---

## Answer JSON Rules
MULTIPLE_CHOICE / SELECT_MULTIPLE / TRUE_FALSE:
{ "correctAnswers": ["1", "3"] }

SHORT_ANSWER:
{ "correctAnswers": ["text"] }

MATCHING:
{ "correctAnswers": [{"leftHeader":"1","rightHeader":"3"},{"leftHeader":"2","rightHeader":"1"}] }

REORDERING:
{ "correctAnswers": ["1","3","4","2"] }

FILL_IN_THE_BLANK:
{ "correctAnswers": ["oxygen","hydrogen"] }

---

## Agent Execution Rules
- Do NOT ask clarifying questions
- Infer missing information
- Maintain difficulty and concept scope
- Preserve logical symbols (¬, ∧, ∨, →, ↔)
- Output UTF‑8 Markdown only
- NO comments, NO explanations
- Save as `/questions/generated/[lesson_file_name].md`
