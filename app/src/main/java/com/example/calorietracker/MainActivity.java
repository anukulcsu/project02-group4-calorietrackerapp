package com.example.calorietracker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button signupBotton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserDAO dao = AppDatabase.getInstance(this).getUserDAO();
        User testUser=dao.getUserByUsername("testuser1");
        if(testUser ==null){
            User user1= new User("testuser1","testuser1",false);
            User user2= new User("admin2","admin2", true);
            dao.insert(user1);
            dao.insert(user2);
        }
        SharedPreferences preferences = getSharedPreferences("CT_PREFS", Context.MODE_PRIVATE);
        if(preferences.contains("USER_ID")){
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
        }
        loginButton=findViewById(R.id.mainLoginButton);
        signupBotton=findViewById(R.id.mainSignupButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

}