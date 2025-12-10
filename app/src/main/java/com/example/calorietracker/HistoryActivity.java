package com.example.calorietracker;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.CalorieHistory;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    Spinner dateSpinner;
    TextView tvDate, tvTotal, tvTarget, tvResult;
    AppDatabase db;
    int currentUserId;
    List<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = AppDatabase.getInstance(this);
        SharedPreferences preferences = getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
        currentUserId = preferences.getInt("USER_ID", -1);

        dateSpinner = findViewById(R.id.dateSpinner);
        tvDate = findViewById(R.id.tvHistoryDate);
        tvTotal = findViewById(R.id.tvHistoryTotal);
        tvTarget = findViewById(R.id.tvHistoryTarget);
        tvResult = findViewById(R.id.tvHistoryResult);

        loadDatesIntoSpinner();
    }

    private void loadDatesIntoSpinner() {
        dates = db.getCalorieHistoryDAO().getAllDates(currentUserId);

        if (dates.isEmpty()) {
            Toast.makeText(this, "No history found", Toast.LENGTH_SHORT).show();
            dates = new ArrayList<>();
            dates.add("No Data");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dates);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = dates.get(position);
                if (!selectedDate.equals("No Data")) {
                    loadHistoryData(selectedDate);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadHistoryData(String date) {
        CalorieHistory history = db.getCalorieHistoryDAO().getHistoryByDate(currentUserId, date);
        if (history != null) {
            tvDate.setText("Date: " + history.date);
            tvTotal.setText("Calories Consumed: " + history.totalCalories);
            tvTarget.setText("Target Goal: " + history.targetCalories);

            if (history.totalCalories > history.targetCalories) {
                tvResult.setText("Status: Over Limit ❌");
                tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvResult.setText("Status: Within Goal ✅");
                tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }
}