package com.example.calorietracker;
import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.CalorieHistory;
import com.example.calorietracker.database.FoodLog;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;
import com.example.calorietracker.database.FoodLogDAO;
import com.example.calorietracker.database.CalorieHistoryDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AppDatabase db;
    private UserDAO userDAO;
    private FoodLogDAO foodLogDAO;
    private CalorieHistoryDAO historyDAO;
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDAO = db.getUserDAO();
        foodLogDAO = db.getFoodLogDAO();
        historyDAO = db.getCalorieHistoryDAO();
    }
    @After
    public void closeDb() throws IOException {
        db.close();
    }
    @Test
    public void insertUser() {
        User user = new User("test1", "pass", false);
        userDAO.insert(user);
        assertNotNull(userDAO.getUserByUsername("test1"));
    }
    @Test
    public void updateUser() {
        User user = new User("updateMe", "oldPass", false);
        userDAO.insert(user);
        User savedUser = userDAO.getUserByUsername("updateMe");
        savedUser.password = "newPass";
        userDAO.updateUser(savedUser);
        User updated = userDAO.getUserByUsername("updateMe");
        assertEquals("newPass", updated.getPassword());
    }
    @Test
    public void deleteUser() {
        User user = new User("deleteMe", "pass", false);
        userDAO.insert(user);
        User saved = userDAO.getUserByUsername("deleteMe");
        userDAO.deleteUser(saved);
        assertNull(userDAO.getUserByUsername("deleteMe"));
    }
    @Test
    public void insertFoodLog() {
        FoodLog food = new FoodLog(1, "Apple", 95);
        foodLogDAO.insert(food);
        List<FoodLog> list = foodLogDAO.getFoodsForUser(1);
        assertEquals(1, list.size());
    }
    @Test
    public void updateFoodLog() {
        foodLogDAO.insert(new FoodLog(1, "Banana", 100));
        foodLogDAO.clearFoodsForUser(1);
        assertTrue(foodLogDAO.getFoodsForUser(1).isEmpty());
    }
    @Test
    public void deleteFoodLog() {
        FoodLog food = new FoodLog(1, "Burger", 500);
        foodLogDAO.insert(food);
        List<FoodLog> list = foodLogDAO.getFoodsForUser(1);
        int id = list.get(0).id;
        foodLogDAO.deleteFoodItem(id);
        assertTrue(foodLogDAO.getFoodsForUser(1).isEmpty());
    }
    @Test
    public void insertHistory() {
        CalorieHistory h = new CalorieHistory(1, "01/01/2025", 2000, 2000);
        historyDAO.insert(h);
        assertNotNull(historyDAO.getHistoryByDate(1, "01/01/2025"));
    }
    @Test
    public void updateHistory() {
        CalorieHistory h = new CalorieHistory(1, "02/01/2025", 1500, 2000);
        historyDAO.insert(h);
        CalorieHistory saved = historyDAO.getHistoryByDate(1, "02/01/2025");
        saved.totalCalories = 1800;
        historyDAO.update(saved);
        CalorieHistory updated = historyDAO.getHistoryByDate(1, "02/01/2025");
        assertEquals(1800, updated.totalCalories);
    }
    @Test
    public void deleteHistory() {
        CalorieHistory h = new CalorieHistory(1, "03/01/2025", 2000, 2000);
        historyDAO.insert(h);

        CalorieHistory saved = historyDAO.getHistoryByDate(1, "03/01/2025");
        historyDAO.delete(saved);

        assertNull(historyDAO.getHistoryByDate(1, "03/01/2025"));
    }
}