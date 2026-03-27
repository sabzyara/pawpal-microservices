from google import genai
import os
from dotenv import load_dotenv
from app.pet_client import get_pet_data

load_dotenv()

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

def ask_ai(question: str, pet_id: str, token: str):
    try:
        pet_data = get_pet_data(pet_id, token)

        if "error" in pet_data:
            return {"error": "Cannot get pet data"}

        if not pet_data.get("pet"):
            return {"error": "Pet not found"}

        prompt = f"""
        You are a professional pet health assistant.

        Pet data:
        {pet_data["pet"]}

        Recent activity:
        {pet_data.get("activities", [])}

        Recent nutrition:
        {pet_data.get("nutrition", [])}

        User question:
        {question}

        Analyze:
        - activity level
        - nutrition quality
        - overall health

        Give specific advice.
        """

        response = client.models.generate_content(
            model="gemini-flash-latest",
            contents=prompt
        )

        return {"response": response.text}

    except Exception as e:
        return {"error": str(e)}