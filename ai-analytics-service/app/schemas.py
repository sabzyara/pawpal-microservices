from pydantic import BaseModel
from typing import List

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


class ChatRequest(BaseModel):
    message: str
    petId: str
    token: str