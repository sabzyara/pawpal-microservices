from app.model import  HealthModel

model = HealthModel()

def predict_health(age, activity):
    result = model.predict(age, activity)
    return "HIGH_RISK" if result == 1 else "LOW_RISK"
