class RecommendationModel:

    def analyze(self, data: dict) -> dict:
        recommendations: list[str] = []

        species = data.get("species", "").lower()
        breed = data.get("breed", "").lower()
        weight = float(data.get("weight", 0))
        age = int(data.get("age", 0))
        activity = int(data.get("totalActivityMinutes", 0))
        calories = int(data.get("totalCalories", 0))
        health_status = data.get("healthStatus", "").lower()

        score = 100

        # -----------------------------
        # Species activity norms
        # -----------------------------
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

        expected_activity = activity_norms.get(
            species,
            activity_norms["default"]
        )

        expected_calories = int(
            weight * calorie_multiplier.get(
                species,
                calorie_multiplier["default"]
            )
        )

        # -----------------------------
        # Activity analysis
        # -----------------------------
        if activity < expected_activity * 0.5:
            recommendations.append(
                "Daily activity level is critically below the recommended range"
            )
            score -= 30

        elif activity < expected_activity:
            recommendations.append(
                "Consider increasing daily physical activity"
            )
            score -= 15

        else:
            recommendations.append(
                "Physical activity level is within the healthy range"
            )

        # -----------------------------
        # Nutrition analysis
        # -----------------------------
        if calories > expected_calories * 1.25:
            recommendations.append(
                "Calorie intake exceeds the recommended daily amount"
            )
            score -= 20

        elif calories < expected_calories * 0.7:
            recommendations.append(
                "Daily calorie intake may be insufficient"
            )
            score -= 20

        else:
            recommendations.append(
                "Nutrition balance appears appropriate"
            )

        # -----------------------------
        # Species-specific recommendations
        # -----------------------------
        if species == "cat" and activity < 20:
            recommendations.append(
                "Increase interactive play sessions for mental stimulation"
            )
            score -= 10

        if species == "dog" and activity < 40:
            recommendations.append(
                "Additional outdoor walks are recommended"
            )
            score -= 10

        # -----------------------------
        # Age analysis
        # -----------------------------
        if age >= 8:
            recommendations.append(
                "Senior pet monitoring is recommended"
            )

            if activity > expected_activity * 1.3:
                recommendations.append(
                    "Avoid excessive physical нагрузки for senior pets"
                )
                score -= 5

        # -----------------------------
        # Health condition analysis
        # -----------------------------
        if "obesity" in health_status:
            recommendations.append(
                "Weight control and veterinary supervision are recommended"
            )
            score -= 15

        if "diabetes" in health_status:
            recommendations.append(
                "Stable nutrition schedule should be maintained"
            )
            score -= 15

        # -----------------------------
        # Breed-specific logic
        # -----------------------------
        if breed in ["labrador", "beagle"] and calories > expected_calories:
            recommendations.append(
                "This breed may have increased obesity risk"
            )
            score -= 5

        # -----------------------------
        # Missing data analysis
        # -----------------------------
        if calories == 0:
            recommendations.append(
                "No nutrition records were detected"
            )
            score -= 15

        if activity == 0:
            recommendations.append(
                "No activity records were detected"
            )
            score -= 20

        # -----------------------------
        # Final score normalization
        # -----------------------------
        score = max(score, 0)

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