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
    private Button addButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemNameInput = findViewById(R.id.itemNameInput);
        numCaloriesInput = findViewById(R.id.numCaloriesInput);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);

        addButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString().trim();
            String caloriesStr = numCaloriesInput.getText().toString().trim();

            // Validate user input
            if (!name.isEmpty() && !caloriesStr.isEmpty()) {
                if (name.length() > 25) { // Prevents extra long names from breaking the formatting
                    Toast.makeText(this, "Name character limit is 25!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int calories = Integer.parseInt(caloriesStr);
                    if (calories > 9999) { // Prevents large numbers from breaking the formatting
                        Toast.makeText(this, "Calorie limit for one entry is 9999!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (calories <= 0) { // Prevents excess entries that do not affect the total
                        Toast.makeText(this, "Entries cannot be 0 calories!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Send user-specified item attributes to main page
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("foodName", name);
                    resultIntent.putExtra("calories", calories);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid input for calories!", Toast.LENGTH_SHORT).show();
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
