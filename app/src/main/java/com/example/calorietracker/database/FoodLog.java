package com.example.calorietracker.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_logs")
public class FoodLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String foodName;
    public int calories;

    public FoodLog(int userId, String foodName, int calories) {
        this.userId = userId;
        this.foodName = foodName;
        this.calories = calories;
    }
}