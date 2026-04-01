**TASK:** You are an expert curriculum designer. Analyze the provided exercises and group them into meaningful, skill-based categories.

**CONTEXT:**

* Lesson ID: %s
* Lesson Name: %s
* Original Category ID: %s
* Original Category Name: %s

**GOAL:**
Create **as many meaningful, distinct skill-based categories as possible** while ensuring each category represents a clear and specific concept or skill. Prefer splitting into smaller conceptual groups rather than merging unrelated skills into broad categories.

**REQUIREMENTS:**

1. **Conceptual Grouping:** Categories must reflect specific skills or concepts (e.g., "Graph Traversal", "Calculating Derivatives"), NOT question formats (e.g., "Multiple Choice").
2. **Maximize Category Count:** Create the largest reasonable number of distinct categories. Avoid overly broad categories like "General Practice" or "Mixed Problems".
3. **Fine-Grained Skills:** If exercises test slightly different skills, place them into different categories.
4. **Exhaustive Mapping:** Assign EVERY provided exercise ID to exactly ONE category. Do not leave any IDs out, and do not invent new ones.
5. **Balanced Grouping:** Categories can contain 1 or more exercises.
6. **Accurate Mapping:** Each exercise ID must appear in exactly one category.
7. **JSON Only:** Do not include explanations or text outside the JSON.

**OUTPUT FORMAT:**

* The JSON output MUST be an Array.
* The very first character of your response must be `[` and the last must be `]`.

**SCHEMA:**
Ensure your JSON array of objects strictly follows this interface:

```typescript
interface CategoryGrouping {
  name: string; // The conceptual skill name
  code: string; // UPPER_SNAKE_CASE_CODE
  type: "Exercise Category"; // Hardcoded exactly like this
  description: string; // 1-2 sentences describing the skill and why it matters
  exerciseIds: string[]; // Array of the original input exercise UUIDs
}
```

**INPUT EXERCISES:**
%s
