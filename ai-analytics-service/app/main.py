from fastapi import FastAPI
from app.schemas import RecommendationRequest, RecommendationResponse, ChatRequest
from app.predictor import generate_recommendations
from app.huggingface_client import ask_ai

app = FastAPI(title="PawPal AI Analytics Service")

@app.post("/ai/recommend", response_model=RecommendationResponse)
def recommend(request: RecommendationRequest):
    return generate_recommendations(request.dict())


@app.post("/ai/chat")
def chat(req: ChatRequest):
    return ask_ai(req.message)