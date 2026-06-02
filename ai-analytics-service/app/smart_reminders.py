from app.notifications import send_smart_notification

def check_pet_data():

    print("Checking pets...")

    send_smart_notification(
        1,
        "Tracker Reminder",
        "Don't forget to update today's tracker."
    )