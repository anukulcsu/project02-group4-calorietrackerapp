package com.example.calorietracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
        targetDisplayView = findViewById(R.id.targetDisplay); //Check this
        comparisonView = findViewById(R.id.comparison);

        int caloriesConsumed = Integer.parseInt(calorieCountView.getText().toString());
        int target = Integer.parseInt(targetDisplayView.getText().toString());

        if (caloriesConsumed < target) {
            comparisonView.setText("<");
        } else if (caloriesConsumed > target) {
            comparisonView.setText(">");
        } else {
            comparisonView.setText("=");
        }
    }

}
