package com.example.calorietracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserDAO;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;
    Button signupButton;
    TextView loginLink;
    UserDAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        dao = AppDatabase.getInstance(this).getUserDAO();
        usernameField = findViewById(R.id.signupUsername);
        passwordField = findViewById(R.id.signupPassword);
        signupButton = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.linkToLogin);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameField.getText().toString();
                String password=passwordField.getText().toString();
                User existingUser=dao.getUserByUsername(username);
                if(existingUser==null) {
                    User newUser=new User(username, password, false);
                    dao.insert(newUser);
                    Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Username Taken", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}