package com.example.calorietracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LandingPage extends AppCompatActivity {

    private ListView foodListView;
    private TextView calorieCountView;
    private TextView targetDisplayView;
    private TextView comparisonView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        //List of foods
        foodListView = findViewById(R.id.foodList);
        ArrayList<String> foods = new ArrayList<>();
        //Placeholder foods
        foods.add("Apple");
        foods.add("Pancakes");
        foods.add("Orange juice");
        foods.add("Potato chips");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foods);
        foodListView.setAdapter(adapter);

        //Compare calories consumed to target
        calorieCountView = findViewById(R.id.calorieCount);
        targetDisplayView = findViewById(R.id.targetDisplay);
        comparisonView = findViewById(R.id.comparison);

        int caloriesConsumed = Integer.parseInt(calorieCountView.getText().toString());
        int target = Integer.parseInt(targetDisplayView.getText().toString());

        updateComparison(caloriesConsumed, target);

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
                                updateComparison(calConsumed, targetInt);
                            } catch (NumberFormatException e) {
                                Toast.makeText(LandingPage.this, "Enter valid value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
        });

    }

    private void updateComparison(int calories, int target) {
        if (calories < target) {
            comparisonView.setText("<");
        } else if (calories > target) {
            comparisonView.setText(">");
        } else {
            comparisonView.setText("=");
        }
    }

}
