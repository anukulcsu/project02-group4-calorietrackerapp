package com.example.calorietracker.database;

import androidx.room.Database;
import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, FoodLog.class, CalorieHistory.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DBName ="CT_database";
    private static AppDatabase instance;
    public abstract UserDAO getUserDAO();
    public static AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                UserDAO dao = getInstance(context).getUserDAO();
                                if (dao.getUserByUsername("admin2") == null) {
                                    dao.insert(new User("admin2", "admin2", true)); // admin info
                                }

                                if (dao.getUserByUsername("testuser1") == null) {
                                    dao.insert(new User("testuser1", "testuser1", false));
                                }
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
}
