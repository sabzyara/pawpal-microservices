class RecommendationModel:

    def analyze(self, data: dict) -> dict:
        recommendations: list[str] = []

        species = data.get("species", "").lower()
        weight = float(data.get("weight", 0))
        age = int(data.get("age", 0))
        activity = int(data.get("totalActivityMinutes", 0))
        calories = int(data.get("totalCalories", 0))

        # --- нормы ---
        activity_norms = {
            "dog": 60,
            "cat": 30,
            "default": 40
        }

        calorie_multiplier = {
            "dog": 60,
            "cat": 50,
            "default": 55
        }

        expected_activity = activity_norms.get(species, activity_norms["default"])
        expected_calories = int(weight * calorie_multiplier.get(species, calorie_multiplier["default"]))

        # --- базовый health score ---
        score = 100

        # ===== АКТИВНОСТЬ =====
        if activity < expected_activity * 0.6:
            recommendations.append("Daily activity level is significantly below recommended range")
            score -= 30
        elif activity < expected_activity:
            recommendations.append("Consider slightly increasing daily activity")
            score -= 15

        # ===== КАЛОРИИ =====
        if calories > expected_calories * 1.2:
            recommendations.append("Daily calorie intake appears too high for current activity level")
            score -= 20
        elif calories < expected_calories * 0.7:
            recommendations.append("Calorie intake may be insufficient for healthy maintenance")
            score -= 20
        else:
            recommendations.append("Daily calorie intake is within the normal range")

        # ===== ПОВЕДЕНИЕ =====
        if species == "cat" and activity < 20:
            recommendations.append("Increase play sessions to improve mental stimulation")
            score -= 10

        if species == "dog" and activity < 40:
            recommendations.append("Additional outdoor walks are recommended")
            score -= 10

        # ===== ВОЗРАСТ =====
        if age > 8 and activity > expected_activity:
            recommendations.append("For senior pets, monitor activity to avoid joint overload")
            score -= 5

        # --- защита от отрицательных значений ---
        score = max(score, 0)

        # ===== RISK LEVEL =====
        if score >= 80:
            risk_level = "LOW"
        elif score >= 50:
            risk_level = "MEDIUM"
        else:
            risk_level = "HIGH"

        return {
            "healthScore": score,
            "riskLevel": risk_level,
            "recommendations": recommendations
        }
