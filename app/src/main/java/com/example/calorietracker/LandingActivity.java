package com.example.calorietracker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        SharedPreferences preferences = getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
        String username = preferences.getString("USERNAME", "Admin");

        TextView welcomeText = findViewById(R.id.landingWelcome);
        welcomeText.setText("Hello, " + username);
        View cardDashboard = findViewById(R.id.cardDashboard);
        cardDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
        View cardAdmin = findViewById(R.id.cardAdminPanel);
        cardAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, AdminPanelActivity.class);
            startActivity(intent);
        });
        Button logoutButton = findViewById(R.id.landingLogoutButton);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}