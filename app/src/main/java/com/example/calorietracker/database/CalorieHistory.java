package com.example.calorietracker.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calorie_history")
public class CalorieHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String date;
    public int totalCalories;
    public int targetCalories;

    public CalorieHistory(int userId, String date, int totalCalories, int targetCalories) {
        this.userId = userId;
        this.date = date;
        this.totalCalories = totalCalories;
        this.targetCalories = targetCalories;
    }
}