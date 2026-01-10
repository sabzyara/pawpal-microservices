package com.example.pawpalapp.petmanagementservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "nutrition_logs")
public class NutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private Long petId;

    private LocalDate date;

    private String mealType;

    private float calories;

    @ElementCollection
    @CollectionTable(name = "nutrition_food_items")
    private List<String> foodItems;


    public void addMeal() {}

    public void editMeal() {}

    public void deleteMeal() {}

    public float calculateCalories() {
        return calories;
    }

    public String generateNutritionSummary() {
        return mealType + " with " + calories + " kcal";
    }


}
