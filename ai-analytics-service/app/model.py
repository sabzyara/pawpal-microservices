class RecommendationModel:

    def analyze(self, data: dict) -> list[str]:
        recs = []

        species = data.get("species")
        weight = int(data.get("weight", 0))
        age = int(data.get("age", 0))
        activity = int(data.get("totalActivityMinutes", 0))
        calories = int(data.get("totalCalories", 0))

        if activity < 30:
            recs.append("Increase daily activity level")

        if weight > 0 and calories > weight * 70:
            recs.append("Daily calorie intake may be too high")

        if species == "cat" and activity < 20:
            recs.append("Increase play sessions for mental stimulation")

        if not recs:
            recs.append("Current routine looks balanced")

        return recs
