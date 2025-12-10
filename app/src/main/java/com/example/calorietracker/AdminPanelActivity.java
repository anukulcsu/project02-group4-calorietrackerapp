package com.example.calorietracker;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calorietracker.database.AppDatabase;
import com.example.calorietracker.database.User;
import com.example.calorietracker.database.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class AdminPanelActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton btnAddUser;
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Admin Channel";
            String description = "Channel for admin actions";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNewUserNotification(String username) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        int notificationId = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_add)
                .setContentTitle("New User Created!")
                .setContentText("Admin has created user: " + username)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
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