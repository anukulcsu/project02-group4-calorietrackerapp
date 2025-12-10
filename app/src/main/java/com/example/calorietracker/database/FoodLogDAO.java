package com.example.calorietracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FoodLogDAO {
    @Insert
    void insert(FoodLog food);

    @Query("SELECT * FROM food_logs WHERE userId = :userId")
    List<FoodLog> getFoodsForUser(int userId);

    @Query("DELETE FROM food_logs WHERE userId = :userId")
    void clearFoodsForUser(int userId);

    @Query("DELETE FROM food_logs WHERE id = :id")
    void deleteFoodItem(int id);
}