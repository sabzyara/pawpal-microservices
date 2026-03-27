import requests

PET_SERVICE_URL = "http://localhost:8081"

import requests

def get_pet_data(pet_id: str, token: str):
    headers = {
        "Authorization": f"Bearer {token}"
    }

    response = requests.get(
        f"http://localhost:8081/api/pets/pet/{pet_id}/full",
        headers=headers
    )

    if response.status_code != 200:
        return {"error": "Failed to fetch pet data"}

    return response.json()