package com.example.calorietracker;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton btnAddUser;
    List<User> userList;
    UserAdapter adapter;
    AppDatabase db;

    private static final String CHANNEL_ID = "admin_channel_v2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        createNotificationChannel();
        db = AppDatabase.getInstance(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerView);
        btnAddUser = findViewById(R.id.btnAddUser);
        findViewById(R.id.btnBackToHub).setOnClickListener(v -> {
            finish();
        });
        adapter = new UserAdapter(this, new ArrayList<>(), db);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.getUserDAO().getAllUsers().observe(this, users -> {
            adapter.setUsers(users);
        });

        btnAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void loadUsers() {

        userList = db.getUserDAO().getAllUsers();

        adapter = new UserAdapter(this, userList, db);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New User");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputUser = new EditText(this);
        inputUser.setHint("Username");
        layout.addView(inputUser);

        final EditText inputPass = new EditText(this);
        inputPass.setHint("Password");
        layout.addView(inputPass);
        builder.setView(layout);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String uName = inputUser.getText().toString();
            String uPass = inputPass.getText().toString();

            if (!uName.isEmpty() && !uPass.isEmpty()) {
                if (db.getUserDAO().getUserByUsername(uName) == null) {
                    User newUser = new User();
                    newUser.username = uName;
                    newUser.password = uPass;
                    newUser.isAdmin = false;
                    db.getUserDAO().insert(newUser);

                    sendNewUserNotification(uName);
                    Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}