package com.example.calorietracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addItemLauncher;
    private ListView foodListView;
    private TextView calorieCountView;
    private TextView targetDisplayView;
    private TextView comparisonView;
    private ArrayList<String> foods;
    private ArrayAdapter<String> adapter;
    public ArrayList<String[]> history;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createNotificationChannel();
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
        tvQuote = findViewById(R.id.tvQuote);
        foodStrings = new ArrayList<>();
        foodObjects = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodStrings);
        foodListView.setAdapter(adapter);

        loadUserData(loggedInUser);
        loadFoodLogs();

        tvQuote.setOnClickListener(v -> {
            tvQuote.setText("Loading new motivation...");
            fetchQuote();
        });
        if (isAdmin) {
            btnBackToAdmin.setVisibility(View.VISIBLE);
            btnBackToAdmin.setOnClickListener(v -> {
                Intent intent = IntentFactory.getLandingIntent(getApplicationContext());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            btnBackToAdmin.setVisibility(View.GONE);
        }

        history = new ArrayList<>();

                        Intent intent = IntentFactory.getLoginIntent(DashboardActivity.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Remove list entries by tapping them
        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = foods.get(position);
            String selectedItemName = selectedItem.substring(0, selectedItem.indexOf(":"));
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Remove Item")
                    .setMessage("Would you like to remove " + selectedItem.foodName + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int caloriesRemoved = retrieveCalories(selectedItem);
                        if (caloriesRemoved != -1) {
                            foods.remove(position);
                            adapter.notifyDataSetChanged();
                            int prevTotal = Integer.parseInt(calorieCountView.getText().toString().trim());
                            int newTotal = prevTotal - caloriesRemoved;
                            calorieCountView.setText(String.valueOf(newTotal));
                            updateComparison(newTotal);
                        } else {
                            Toast.makeText(this, "Error removing item!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        Button editTargetButton = findViewById(R.id.editTarget);
        editTargetButton.setOnClickListener(v -> {
            TargetFragment fragment = new TargetFragment();
            fragment.show(getSupportFragmentManager(), "TargetFragment");
        });

        int caloriesConsumed = Integer.parseInt(calorieCountView.getText().toString());
        updateComparison(caloriesConsumed);

        // Set/edit calorie target via pop-up from button
        Button editTargetButton = findViewById(R.id.editTarget);
        editTargetButton.setOnClickListener(v -> {
            EditText input = new EditText(DashboardActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Set Calorie Target")
                    .setMessage("Enter your daily calorie target")
                    .setView(input)
                    .setPositiveButton("Set", (dialog, which) -> {
                        String targetStr = input.getText().toString().trim();
                        if (!targetStr.isEmpty()) {
                            try {
                                int targetInt = Integer.parseInt(targetStr);
                                if (targetInt > 99999) {
                                    Toast.makeText(DashboardActivity.this, "Limit is 99999!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                targetDisplayView.setText(String.valueOf(targetInt));
                                int calConsumed = Integer.parseInt(calorieCountView.getText().toString());
                                updateComparison(calConsumed);
                            } catch (NumberFormatException e) {
                                Toast.makeText(DashboardActivity.this, "Enter valid value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
        });
        // Handles user-specified item properties from add item activity
        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String foodName = data.getStringExtra("foodName");
                        int calories = data.getIntExtra("calories", 0);
                        FoodLog newLog = new FoodLog(currentUserId, foodName, calories);
                        db.getFoodLogDAO().insert(newLog);
                        loadFoodLogs();
                    }
                }
        );

        Button addItemButton = findViewById(R.id.addItem);
        addItemButton.setOnClickListener(v -> {
            Intent intent = IntentFactory.getAddItemIntent(DashboardActivity.this);
            addItemLauncher.launch(intent);
        });

        Button logCaloriesButton = findViewById(R.id.logCalories);
        logCaloriesButton.setOnClickListener(v -> showDatePickerAndReset());

        Button tipsButton = findViewById(R.id.historyButton);
        tipsButton.setOnClickListener(v -> {
            Intent intent = IntentFactory.getHistoryIntent(DashboardActivity.this);
            startActivity(intent);
        });
        // 🔹 END TIPS BUTTON

        TextView loggedInUser = findViewById(R.id.userLoggedIn);
        // Display username of user currently logged in
        SharedPreferences preferences = getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
        int userId = preferences.getInt("USER_ID", -1);
        if (userId != -1) {
            UserDAO dao = AppDatabase.getInstance(this).getUserDAO();
            User user = dao.getUserById(String.valueOf(userId));
            if (user != null) {
                loggedInUser.setText("Logged in as: " + user.getUsername());
            }
        }
        // Allow user to sign out by clicking their username
        loggedInUser.setOnClickListener(v -> {
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Do you want to sign out and return to login?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    // Compares calories consumed to target and updates summary
    private void updateComparison(int calories) {
        int target = Integer.parseInt(targetDisplayView.getText().toString());
        comparisonView.setText(getComparison(calories, target));
    }

    static String getComparison(int calories, int target) {
        if (calories < target) {
            return "<";
        } else if (calories > target) {
            return ">";
        } else {
            return "=";
        }
    }

    @Override
    public void onTargetSaved(int newTarget) {
        targetDisplayView.setText(String.valueOf(newTarget));
        String currentCals = calorieCountView.getText().toString();
        if (!currentCals.isEmpty()) {
            updateComparison(Integer.parseInt(currentCals));
        }
    }

    private void fetchQuote() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuoteApi api = retrofit.create(QuoteApi.class);

        api.getRandomQuote().enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvQuote.setText("\"" + response.body().quote + "\"\n- " + response.body().author);
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                tvQuote.setText("Stay focused on your goals!");
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Goal Channel";
            String description = "Channel for calorie goals";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendGoalNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        int notificationId = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Goal Reached! 🎉")
                .setContentText("Good job! You have hit your calorie target.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
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
        String currentCalsStr = calorieCountView.getText().toString();
        String targetStr = targetDisplayView.getText().toString();
        int total = currentCalsStr.isEmpty() ? 0 : Integer.parseInt(currentCalsStr);
        int target = targetStr.isEmpty() ? 0 : Integer.parseInt(targetStr);

        CalorieHistory history = new CalorieHistory(currentUserId, date, total, target);
        db.getCalorieHistoryDAO().insert(history);
        db.getFoodLogDAO().clearFoodsForUser(currentUserId);

        isGoalNotified = false;
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

        String comparison = getComparison(calories, target);
        comparisonView.setText(comparison);

        if (calories >= target && !isGoalNotified) {
            sendGoalNotification();
            isGoalNotified = true;
        }
        if (calories < target) {
            isGoalNotified = false;
        }
    }

    private void loadUserData(TextView view) {
        if (currentUserId != -1) {
            User user = db.getUserDAO().getUserById(String.valueOf(currentUserId));
            if (user != null) view.setText("Logged in as: " + user.getUsername());
        }
    }
}
