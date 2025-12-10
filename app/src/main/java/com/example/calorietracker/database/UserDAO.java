package com.example.calorietracker.database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
@Dao
public interface UserDAO {
    @Insert
    void insert(User users);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(String userId);
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
    @Delete
    void deleteUser(User user);
    @Update
    void updateUser(User user);
}