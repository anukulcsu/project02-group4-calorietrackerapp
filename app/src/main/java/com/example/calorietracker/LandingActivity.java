package com.example.calorietracker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;

public class LandingActivity extends AppCompatActivity {

    TextView welcomeText;
    Button adminButton;
    Button logoutButton;
    UserDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        dao = AppDatabase.getInstance(this).getUserDAO();
        welcomeText = findViewById(R.id.landingWelcome);
        adminButton = findViewById(R.id.landingAdminButton);
        logoutButton = findViewById(R.id.landingLogoutButton);
        SharedPreferences preferences = getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
        int userId = preferences.getInt("USER_ID", -1);
        boolean isAdmin = preferences.getBoolean("IS_ADMIN", false);

        if (userId != -1) {
            User user = dao.getUserById(String.valueOf(userId));
            if(user != null) {
                welcomeText.setText("Welcome " + user.getUsername());
            }
        }

        if(isAdmin) {
            adminButton.setVisibility(View.VISIBLE);
        } else {
            adminButton.setVisibility(View.GONE);
        }

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, AdminPanelActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
