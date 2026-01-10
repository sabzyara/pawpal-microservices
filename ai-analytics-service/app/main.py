from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from app.predictor import generate_recommendations

app = FastAPI()   # ← ВОТ ЭТО КЛЮЧЕВО

class RecommendationRequest(BaseModel):
    species: str
    weight: int
    age: int
    totalActivityMinutes: int
    totalCalories: int

class RecommendationResponse(BaseModel):
    healthScore: int
    riskLevel: str
    recommendations: List[str]

@app.post("/ai/recommend", response_model=RecommendationResponse)
def recommend(request: RecommendationRequest):
    return generate_recommendations(request.dict())
t
