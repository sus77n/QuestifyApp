# Prompt for Generating Questions

## Task
Generate a new practice question file based on the content in  
**/questions/original/*.md**  
and save the result as a new file named  
**/questions/generated/*.md**

## Requirements
Generate a total of **200 new questions**:
- 100 questions of type: `MULTIPLE_CHOICE`
- 100 questions of type: `SHORT_ANSWER`

## Format
Each question must strictly follow the format defined in  
**/templates/templateLesson.md**, including the following fields:

- **EX <lesson_id>.<question_number>**
- **Type**: (either `MULTIPLE_CHOICE` or `SHORT_ANSWER`)
- **Question**: the main question text
- **Options**: (only for Multiple Choice questions; exactly 4 options)
- **Solution**: the correct answer (text or option letter)

---

## Guidelines for Content (General)

### For All Questions
- All content must be **derived from or inspired by** the concepts, notations, and patterns in the source file.
- Do **not duplicate** any question from the original source file.
- Maintain consistent style and terminology as used in the original lesson.
- Ensure variety in topics and logic depth within the lesson scope.

### For Multiple Choice
- Each question must present **exactly 4 distinct answer options** (labeled A–D), with only **one correct answer**.
- Options should be plausible to encourage reasoning.
- Topics may include factual knowledge, symbolic reasoning, equivalence, or applied logic depending on the lesson content.
- Use clear and concise language for both the question and the options.

### For Short Answer
- Answers should be short, precise, and easy to validate (typically one line).
- It is allowed to use logic symbols such as `¬`, `∧`, `∨`, `→`, `↔`, **or** write them using plain text equivalents:
  - `¬` → `not`
  - `∧` → `and`
  - `∨` → `or`
  - `→` → `->`
  - `↔` → `<->`
- Always accept both formats for answers.
- Avoid open-ended definitions or answers requiring full explanations.
- Provide a mix of question types: some should require translation from English to logic, some from logic to English, and some symbolic manipulation.


## Output Instructions
- The output must be a plain Markdown file containing only the formatted questions.
- Save it as:  
  **/questions/generated/lesson1_1_gen1.md**
- Do not include any comments, explanations, or metadata outside the expected format.
