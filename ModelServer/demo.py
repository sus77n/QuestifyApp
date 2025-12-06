import os
import google.generativeai as genai

genai.configure(api_key=os.environ["GEMINI_API_KEY"])
    H
for model in genai.list_models():
    print(model.name)

# file = genai.upload_file("text.md", mime_type="text/markdown")

# model = genai.GenerativeModel("models/gemini-2.5-flash") 

# response = model.generate_content([
#     file,
#     "Read this markdown and rewrite a better version in markdown. Output only markdown."
# ])

# output_md = response.text

# with open("output.md", "w", encoding="utf-8") as f:
#     f.write(output_md)

# print("Saved to output.md")
