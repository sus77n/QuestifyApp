**TASK:** You are an expert curriculum designer. Analyze the provided exercises and group them into meaningful, skill-based categories.

**CONTEXT:**
- Lesson ID: %s
- Lesson Name: %s
- Original Category ID: %s
- Original Category Name: %s

**REQUIREMENTS:**
1. **Conceptual Grouping:** Categories must reflect specific skills or concepts (e.g., "Graph Traversal", "Calculating Derivatives"), NOT question formats (e.g., "Multiple Choice").
2. **Dynamic Categories:** Determine the optimal number of distinct, reusable categories needed to cover all the input exercises.
3. **Exhaustive Mapping:** Assign EVERY provided exercise ID to exactly ONE category. Do not leave any IDs out, and do not invent new ones.
4. **Accurate Counting:** The `numberOfExercise` integer MUST exactly match the number of items in the `exerciseIds` array for that specific category.

**OUTPUT FORMAT:**
- The JSON output MUST be an Array.
- The very first character of your response must be `[` and the last must be `]`.

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