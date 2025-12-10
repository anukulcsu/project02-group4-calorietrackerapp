package com.example.calorietracker;
import android.content.Context;
import android.content.Intent;

public class IntentFactory {
    public static Intent getLoginIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
    public static Intent getDashboardIntent(Context context) {
        return new Intent(context, DashboardActivity.class);
    }
    public static Intent getLandingIntent(Context context) {
        return new Intent(context, LandingActivity.class);
    }
    public static Intent getAdminPanelIntent(Context context) {
        return new Intent(context, AdminPanelActivity.class);
    }
    public static Intent getHistoryIntent(Context context) {
        return new Intent(context, HistoryActivity.class);
    }
    public static Intent getAddItemIntent(Context context) {
        return new Intent(context, AddItemActivity.class);
    }
    public static Intent getSignUpIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}