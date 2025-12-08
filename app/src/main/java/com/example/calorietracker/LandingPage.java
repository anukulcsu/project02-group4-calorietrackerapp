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

public class LandingPage extends AppCompatActivity {

    private ActivityResultLauncher<Intent> addItemLauncher;
    private ListView foodListView;
    private TextView calorieCountView;
    private TextView targetDisplayView;
    private TextView comparisonView;
    private ArrayList<String> foods;
    private ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // Create list of foods
        foodListView = findViewById(R.id.foodList);
        foods = new ArrayList<>(); // Empty by default, items will be supplied by the user
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foods);
        foodListView.setAdapter(adapter);

        // Compare calories consumed to target
        calorieCountView = findViewById(R.id.calorieCount);
        targetDisplayView = findViewById(R.id.targetDisplay);
        comparisonView = findViewById(R.id.comparison);

        int caloriesConsumed = Integer.parseInt(calorieCountView.getText().toString());

        updateComparison(caloriesConsumed);

        // Set/edit calorie target via pop-up from button
        Button editTargetButton = findViewById(R.id.editTarget);
        editTargetButton.setOnClickListener(v -> {
            EditText input = new EditText(LandingPage.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(LandingPage.this)
                    .setTitle("Set Calorie Target")
                    .setMessage("Enter your daily calorie target")
                    .setView(input)
                    .setPositiveButton("Set", (dialog, which) -> {
                        String targetStr = input.getText().toString().trim();
                        if (!targetStr.isEmpty()) {
                            try {
                                int targetInt = Integer.parseInt(targetStr);
                                targetDisplayView.setText(String.valueOf(targetInt));
                                int calConsumed = Integer.parseInt(calorieCountView.getText().toString());
                                updateComparison(calConsumed);
                            } catch (NumberFormatException e) {
                                Toast.makeText(LandingPage.this, "Enter valid value", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(LandingPage.this, AddItemActivity.class);
            addItemLauncher.launch(intent);
        });

    }

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

}
