package com.example.calorietracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.CalorieHistory;
import com.example.calorietracker.database.FoodLog;
import com.example.calorietracker.database.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addItemLauncher;
    private ListView foodListView;
    private TextView calorieCountView;
    private TextView targetDisplayView;
    private TextView comparisonView;
    private ArrayList<String> foodStrings;
    private ArrayList<FoodLog> foodObjects;
    private ArrayAdapter<String> adapter;
    private AppDatabase db;
    private int currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db = AppDatabase.getInstance(this);
        SharedPreferences preferences = getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
        currentUserId = preferences.getInt("USER_ID", -1);
        boolean isAdmin = preferences.getBoolean("IS_ADMIN", false);

        foodListView = findViewById(R.id.foodList);
        calorieCountView = findViewById(R.id.calorieCount);
        targetDisplayView = findViewById(R.id.targetDisplay);
        comparisonView = findViewById(R.id.comparison);
        TextView loggedInUser = findViewById(R.id.userLoggedIn);
        Button btnBackToAdmin = findViewById(R.id.btnBackToAdmin);

        foodStrings = new ArrayList<>();
        foodObjects = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodStrings);
        foodListView.setAdapter(adapter);

        loadUserData(loggedInUser);
        loadFoodLogs();
        if (isAdmin) {
            btnBackToAdmin.setVisibility(View.VISIBLE);
            btnBackToAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            btnBackToAdmin.setVisibility(View.GONE);
        }

        loggedInUser.setOnClickListener(v -> {
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            FoodLog selectedItem = foodObjects.get(position);
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Remove Item")
                    .setMessage("Remove " + selectedItem.foodName + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.getFoodLogDAO().deleteFoodItem(selectedItem.id);
                        foodObjects.remove(position);
                        foodStrings.remove(position);
                        adapter.notifyDataSetChanged();
                        updateTotalCalories();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        findViewById(R.id.editTarget).setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(this)
                    .setTitle("Set Calorie Target")
                    .setView(input)
                    .setPositiveButton("Set", (dialog, which) -> {
                        String val = input.getText().toString();
                        if (!val.isEmpty()) {
                            targetDisplayView.setText(val);
                            updateComparison(Integer.parseInt(calorieCountView.getText().toString()));
                        }
                    })
                    .setNegativeButton("Cancel", null).show();
        });

        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String name = result.getData().getStringExtra("foodName");
                        int cals = result.getData().getIntExtra("calories", 0);
                        FoodLog newLog = new FoodLog(currentUserId, name, cals);
                        db.getFoodLogDAO().insert(newLog);
                        loadFoodLogs();
                    }
                }
        );

        findViewById(R.id.addItem).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddItemActivity.class);
            addItemLauncher.launch(intent);
        });

        findViewById(R.id.logCalories).setOnClickListener(v -> showDatePickerAndReset());

        findViewById(R.id.historyButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePickerAndReset() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    saveHistoryAndReset(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveHistoryAndReset(String date) {
        int total = Integer.parseInt(calorieCountView.getText().toString());
        int target = Integer.parseInt(targetDisplayView.getText().toString());
        CalorieHistory history = new CalorieHistory(currentUserId, date, total, target);
        db.getCalorieHistoryDAO().insert(history);
        db.getFoodLogDAO().clearFoodsForUser(currentUserId);
        loadFoodLogs();
        Toast.makeText(this, "Logged for " + date + " and Resetted!", Toast.LENGTH_SHORT).show();
    }
    private void loadFoodLogs() {
        foodObjects.clear();
        foodStrings.clear();
        List<FoodLog> dbList = db.getFoodLogDAO().getFoodsForUser(currentUserId);
        int totalCal = 0;
        for (FoodLog f : dbList) {
            foodObjects.add(f);
            foodStrings.add(f.foodName + ": " + f.calories + " cal");
            totalCal += f.calories;
        }
        adapter.notifyDataSetChanged();
        calorieCountView.setText(String.valueOf(totalCal));
        updateComparison(totalCal);
    }

    private void updateTotalCalories() {
        int total = 0;
        for (FoodLog f : foodObjects) {
            total += f.calories;
        }
        calorieCountView.setText(String.valueOf(total));
        updateComparison(total);
    }
    private void updateComparison(int calories) {
        String targetStr = targetDisplayView.getText().toString();
        if(targetStr.isEmpty()) return;
        int target = Integer.parseInt(targetStr);
        if (calories < target) comparisonView.setText("<");
        else if (calories > target) comparisonView.setText(">");
        else comparisonView.setText("=");
    }
    private void loadUserData(TextView view) {
        if (currentUserId != -1) {
            User user = db.getUserDAO().getUserById(String.valueOf(currentUserId));
            if (user != null) view.setText("Logged in as: " + user.getUsername());
        }
    }
}