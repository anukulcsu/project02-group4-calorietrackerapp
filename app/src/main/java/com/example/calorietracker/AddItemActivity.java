package com.example.calorietracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameInput;
    private EditText numCaloriesInput;
    private EditText timeInput;
    private Button addButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        itemNameInput = findViewById(R.id.itemNameInput);
        numCaloriesInput = findViewById(R.id.numCaloriesInput);
        timeInput = findViewById(R.id.timeInput);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);

        addButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString().trim();
            String caloriesStr = numCaloriesInput.getText().toString().trim();
            String time = timeInput.getText().toString().toLowerCase().trim();

            // Validate user input
            if (!name.isEmpty() && !caloriesStr.isEmpty() && !time.isEmpty()) {
                // Validate time, must be in 12hr format, HH:MMam/pm
                if (time.charAt(2) == ':' && time.length() == 7 && time.charAt(6) == 'm' && (time.charAt(5) == 'a' || time.charAt(5) == 'p')) {
                    // Ensure hours and minutes are numbers and valid values
                    try {
                        int hour = Integer.parseInt(time.substring(0, 2));
                        int minutes = Integer.parseInt(time.substring(3, 5));
                        if (hour > 12 || minutes > 59) {
                            Toast.makeText(this, "Invalid time format!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid time format!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        // Send user-specified item attributes to main page
                        int calories = Integer.parseInt(caloriesStr);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("foodName", name);
                        resultIntent.putExtra("calories", calories);
                        resultIntent.putExtra("time", time);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid input for calories!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid time format!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

}
