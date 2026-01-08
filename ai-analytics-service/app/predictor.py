# app/predictor.py

from app.model import RecommendationModel

model = RecommendationModel()

def generate_recommendations(data: dict) -> list[str]:
    return model.analyze(data)
