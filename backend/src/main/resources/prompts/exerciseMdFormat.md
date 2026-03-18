**EXERCISE GENERATION RULES BY TYPE:**

**1. MULTIPLE_CHOICE**
- **Logic:** Exactly 4 options (1 correct, 3 distractors). Distractors MUST be logically plausible but incorrect.
- **Answer Schema:** `{ "correctAnswers": ["<header_id>"] }`

**2. SELECT_MULTIPLE**
- **Logic:** 4–6 options (2–3 correct). Distractors should be common mistakes or partially correct.
- **Answer Schema:** `{ "correctAnswers": ["<header_id_1>", "<header_id_2>"] }`

**3. TRUE_FALSE**
- **Logic:** Generate 3–6 statements as options. Evaluate each as True or False.
- **Answer Schema:** `{ "correctAnswers": ["<header_id_of_TRUE_1>", "<header_id_of_TRUE_2>"] }`
  *(CRITICAL: Only include IDs of TRUE statements. Omit FALSE statements entirely).*

**4. MATCHING**
- **Logic:** Generate pairs (e.g., Term -> Definition). Left items use metadata "left", Right items use metadata "right".
- **Answer Schema:** `{ "correctAnswers": [ {"leftHeader": "<left_1>", "rightHeader": "<right_target>"}, ... ] }`

**5. REORDERING**
- **Logic:** A sequence of steps. Options must be provided in a SHUFFLED order.
- **Answer Schema:** `{ "correctAnswers": ["<header_id_step_1>", "<header_id_step_2>", ...] }`

**6. FILL_IN_THE_BLANK**
- **Logic:** A sentence with one or multiple missing words.
- **Answer Schema:** `{ "correctAnswers": ["<exact_word_1>", "<exact_word_2>"] }`

**7. SHORT_ANSWER**
- **Logic:** Open-ended question.
- **Answer Schema:** `{ "correctAnswers": ["<required_keyword_or_phrase>"] }`