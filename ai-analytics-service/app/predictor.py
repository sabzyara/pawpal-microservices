from app.model import RecommendationModel

model = RecommendationModel()

def generate_recommendations(data: dict) -> dict:
    return model.analyze(data)