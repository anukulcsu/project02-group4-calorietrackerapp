package com.example.calorietracker;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.calorietracker.api.Quote;
import com.example.calorietracker.api.QuoteApi;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.CalorieHistory;
import com.example.calorietracker.database.FoodLog;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity implements TargetFragment.OnTargetSavedListener {

    private ActivityResultLauncher<Intent> addItemLauncher;
    private ListView foodListView;
    private TextView calorieCountView;
    private TextView targetDisplayView;
    private TextView comparisonView;
    private TextView tvQuote;
    private ArrayList<String> foodStrings;
    private ArrayList<FoodLog> foodObjects;
    private ArrayAdapter<String> adapter;
    private AppDatabase db;
    private int currentUserId;
    private static final String CHANNEL_ID = "goal_channel_v3";
    private boolean isGoalNotified = false;

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
        tvQuote = findViewById(R.id.tvQuote);
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
                Intent intent = IntentFactory.getLandingIntent(getApplicationContext());
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
                    .setMessage("Do you want to sign out and return to login?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = IntentFactory.getLoginIntent(DashboardActivity.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            FoodLog selectedItem = foodObjects.get(position);
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Remove Item")
                    .setMessage("Would you like to remove " + selectedItem.foodName + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.getFoodLogDAO().deleteFoodItem(selectedItem.id);
                        foodObjects.remove(position);
                        foodStrings.remove(position);
                        adapter.notifyDataSetChanged();
                        updateTotalCalories();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        findViewById(R.id.editTarget).setOnClickListener(v -> {
            TargetFragment fragment = new TargetFragment();
            fragment.show(getSupportFragmentManager(), "TargetFragment");
        });

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

        findViewById(R.id.addItem).setOnClickListener(v -> {
            Intent intent = IntentFactory.getAddItemIntent(DashboardActivity.this);
            addItemLauncher.launch(intent);
        });

        findViewById(R.id.logCalories).setOnClickListener(v -> showDatePickerAndReset());

        findViewById(R.id.historyButton).setOnClickListener(v -> {
            Intent intent = IntentFactory.getHistoryIntent(DashboardActivity.this);
            startActivity(intent);
        });
        tvQuote.setOnClickListener(v -> {
            tvQuote.setText("Loading new motivation...");
            fetchQuote();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchQuote();
    }

    public static int retrieveCalories(String entry) {
        try {
            int start = entry.indexOf(":") + 2;
            int end = entry.indexOf(" cal");
            String caloriesStr = entry.substring(start, end).trim();
            return Integer.parseInt(caloriesStr);
        } catch (Exception e) {
            return -1;
        }
    }
    public static String getComparison(int calories, int target) {
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
            UserDAO dao = db.getUserDAO();
            User user = dao.getUserById(String.valueOf(currentUserId));
            if (user != null) view.setText("Logged in as: " + user.getUsername());
        }
    }
}