package com.example.calorietracker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class LoginActivity extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;
    Button loginButton;
    TextView signupLink;
    UserDAO dao;
    //login page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dao = AppDatabase.getInstance(this).getUserDAO();
        usernameField = findViewById(R.id.loginUsername);
        passwordField = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.linkToSignup);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username=usernameField.getText().toString();
                String password=passwordField.getText().toString();
                User user = dao.getUserByUsername(username);
                if (user != null && user.getPassword().equals(password)) {
                    SharedPreferences preferences=getSharedPreferences("PROJECT2_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("USERNAME",user.getUsername());
                    editor.putInt("USER_ID",user.getUserId());
                    editor.putBoolean("IS_ADMIN",user.isAdmin());
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
