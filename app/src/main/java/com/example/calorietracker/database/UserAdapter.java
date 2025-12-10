package com.example.calorietracker.database;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorietracker.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    List<User> userList;
    AppDatabase db;

    public void setUsers(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }
    public UserAdapter(Context context, List<User> userList, AppDatabase db) {
        this.context = context;
        this.userList = userList;
        this.db = db;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User currentUser = userList.get(position);
        holder.tvUsername.setText(currentUser.username);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete " + currentUser.username + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.getUserDAO().deleteUser(currentUser);
                        userList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, userList.size());
                        Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update User");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            final EditText inputUser = new EditText(context);
            inputUser.setText(currentUser.username);
            layout.addView(inputUser);

            final EditText inputPass = new EditText(context);
            inputPass.setHint("New Password");
            layout.addView(inputPass);

            builder.setView(layout);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newName = inputUser.getText().toString();
                String newPass = inputPass.getText().toString();

                if(!newName.isEmpty() && !newPass.isEmpty()) {
                    currentUser.username = newName;
                    currentUser.password = newPass;

                    // Update RoomDB
                    db.getUserDAO().updateUser(currentUser);
                    notifyItemChanged(position);
                    Toast.makeText(context, "User Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        ImageButton btnEdit, btnDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}