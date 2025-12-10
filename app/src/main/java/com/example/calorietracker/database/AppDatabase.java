package com.example.calorietracker.database;

import androidx.room.Database;
import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DBName ="CT_database";
    private static AppDatabase instance;
    public abstract UserDAO getUserDAO();
    public static AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class, DBName).allowMainThreadQueries().build();
        }
        return instance;
    }
}
