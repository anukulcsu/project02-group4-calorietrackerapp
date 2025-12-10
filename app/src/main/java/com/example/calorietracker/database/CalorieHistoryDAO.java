package com.example.calorietracker.database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CalorieHistoryDAO {
    @Insert
    void insert(CalorieHistory history);
    @Update
    void update(CalorieHistory history);
    @Delete
    void delete(CalorieHistory history);
    @Query("SELECT * FROM calorie_history WHERE userId = :userId")
    List<CalorieHistory> getHistoryForUser(int userId);
    @Query("SELECT DISTINCT date FROM calorie_history WHERE userId = :userId")
    List<String> getAllDates(int userId);
    @Query("SELECT * FROM calorie_history WHERE userId = :userId AND date = :date")
    CalorieHistory getHistoryByDate(int userId, String date);
}