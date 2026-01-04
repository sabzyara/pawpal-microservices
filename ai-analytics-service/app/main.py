from fastapi import FastAPI
from pydantic import BaseModel
from app.predictor  import predict_health

app = FastAPI()

class HealthRequest(BaseModel):
    age: int
    activity: int

@app.post("/ai/predict/health")
def predict(request: HealthRequest):
    return {"risk": predict_health(request.age, request.activity)}
