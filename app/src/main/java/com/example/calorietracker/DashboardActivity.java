package com.example.calorietracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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

        history = new ArrayList<>();

        // Create list of foods
        foodListView = findViewById(R.id.foodList);
        foods = new ArrayList<>(); // Empty by default, items will be supplied by the user
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foods);
        foodListView.setAdapter(adapter);

        // Remove list entries by tapping them
        foodListView.setOnItemClickListener((parent, view, position, id) -> {
        String selectedItem = foods.get(position);
        String selectedItemName = selectedItem.substring(0, selectedItem.indexOf(":"));
        new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Remove Item")
                .setMessage("Would you like to remove " + selectedItemName + "?")
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

        // Compare calories consumed to target
        calorieCountView = findViewById(R.id.calorieCount);
        targetDisplayView = findViewById(R.id.targetDisplay);
        comparisonView = findViewById(R.id.comparison);

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
                        String time = data.getStringExtra("time");

                        String entry = foodName + ": " + calories + " cal, " + time;
                        foods.add(entry);
                        adapter.notifyDataSetChanged();

                        int prevTotal = Integer.parseInt(calorieCountView.getText().toString().trim());
                        int newTotal = prevTotal + calories;
                        calorieCountView.setText(String.valueOf(newTotal));

                        updateComparison(newTotal);
                    }
                }
        );

        // Launches add item activity when add item button is pressed
        Button addItemButton = findViewById(R.id.addItem);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddItemActivity.class);
            addItemLauncher.launch(intent);
        });

        // Saves user-specified date, target calories, and calories consumed to history ArrayList for retrieval
        Button logCaloriesButton = findViewById(R.id.logCalories);
        logCaloriesButton.setOnClickListener(v -> {
            EditText input = new EditText(DashboardActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Log Today's Calories and Target")
                    .setMessage("Enter the date")
                    .setView(input)
                    .setPositiveButton("Log & Reset", (dialog, which) -> {
                        String date = input.getText().toString().trim();
                        if (!date.isEmpty()) {
                            // Date, target, calories consumed
                            String[] historyEntry = new String[]{date, targetDisplayView.getText().toString(), calorieCountView.getText().toString()};
                            history.add(historyEntry);
                            foods.clear(); // Clear logged foods
                            adapter.notifyDataSetChanged();
                            calorieCountView.setText("0");
                            int newCalories = Integer.parseInt(calorieCountView.getText().toString());
                            updateComparison(newCalories);
                            Toast.makeText(DashboardActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
        });

    }

    // Compares calories consumed to target and updates summary
    private void updateComparison(int calories) {
        int target = Integer.parseInt(targetDisplayView.getText().toString());
        if (calories < target) {
            comparisonView.setText("<");
        } else if (calories > target) {
            comparisonView.setText(">");
        } else {
            comparisonView.setText("=");
        }
    }

    // Gets calories from list entry for updating total calories upon entry removal
    private int retrieveCalories(String entry) {
        try {
            int start = entry.indexOf(":") + 2;
            int end = entry.indexOf(" cal");
            String caloriesStr = entry.substring(start, end).trim();
            return Integer.parseInt(caloriesStr);
        } catch (Exception e) {
            return -1;
        }
    }

}
