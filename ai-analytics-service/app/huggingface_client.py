from google import genai
import os
from dotenv import load_dotenv

load_dotenv()

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

def ask_ai(question: str):
    try:
        response = client.models.generate_content(
            model="gemini-flash-latest",
            contents=question
        )

        return {
            "response": response.text
        }

    except Exception as e:
        return {"error": str(e)}