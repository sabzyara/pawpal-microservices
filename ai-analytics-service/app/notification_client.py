import requests
from datetime import datetime

NOTIFICATION_URL = (
    "https://pawpal-notification.onrender.com/api/reminders"
)

def send_smart_notification(
        user_id: int,
        title: str,
        message: str
):

    payload = {
        "userId": user_id,
        "type": "SMART_RECOMMENDATION",
        "title": title,
        "message": message,
        "scheduledAt": datetime.now().isoformat()
    }

    try:
        response = requests.post(
            NOTIFICATION_URL,
            json=payload
        )

        print("Notification sent:", response.status_code)

    except Exception as e:
        print("Notification error:", e)