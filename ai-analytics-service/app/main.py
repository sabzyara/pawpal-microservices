from fastapi import FastAPI

from apscheduler.schedulers.background import BackgroundScheduler

from app.schemas import RecommendationRequest, RecommendationResponse, ChatRequest
from app.predictor import generate_recommendations
from app.ai_client import ask_ai
from app.smart_reminders import check_pet_data

app = FastAPI(title="PawPal AI Analytics Service")

@app.on_event("startup")
def start_scheduler():

    scheduler = BackgroundScheduler()

    scheduler.add_job(
        check_pet_data,
        "interval",
        minutes=5  # пока для теста
    )

    scheduler.start()

    print("Scheduler started")

@app.post("/ai/recommend", response_model=RecommendationResponse)
def recommend(request: RecommendationRequest):
    return generate_recommendations(request.dict())


@app.post("/ai/chat")
def chat(req: ChatRequest):
    return ask_ai(
        req.message,
        req.petId,
        req.token,
        req.userId
    )