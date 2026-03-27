from google import genai
import os
from dotenv import load_dotenv
from app.pet_client import get_pet_data

load_dotenv()

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

def ask_ai(question: str, user_id: str, token: str):
    try:
        pet_data = get_pet_data(user_id, token)

        prompt = f"""
        Pet data:
        {pet_data}

        Question:
        {question}
        """

        response = client.models.generate_content(
            model="gemini-flash-latest",
            contents=prompt
        )

        return {"response": response.text}

    except Exception as e:
        return {"error": str(e)}