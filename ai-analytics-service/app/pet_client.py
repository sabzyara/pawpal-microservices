import requests

PET_SERVICE_URL = "http://localhost:8081"

import requests

def get_pet_data(user_id: str, token: str):
    headers = {
        "Authorization": f"Bearer {token}"
    }

    response = requests.get(
        f"http://localhost:8081/api/pets/{user_id}",
        headers=headers
    )

    return response.json()