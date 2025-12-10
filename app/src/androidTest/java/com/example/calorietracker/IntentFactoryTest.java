package com.example.calorietracker;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class IntentFactoryTest {
    private Context context = ApplicationProvider.getApplicationContext();
    @Test
    public void testLoginIntent() {
        Intent intent = IntentFactory.getLoginIntent(context);
        assertEquals(LoginActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testDashboardIntent() {
        Intent intent = IntentFactory.getDashboardIntent(context);
        assertEquals(DashboardActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testLandingIntent() {
        Intent intent = IntentFactory.getLandingIntent(context);
        assertEquals(LandingActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testAdminPanelIntent() {
        Intent intent = IntentFactory.getAdminPanelIntent(context);
        assertEquals(AdminPanelActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testHistoryIntent() {
        Intent intent = IntentFactory.getHistoryIntent(context);
        assertEquals(HistoryActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testAddItemIntent() {
        Intent intent = IntentFactory.getAddItemIntent(context);
        assertEquals(AddItemActivity.class.getName(), intent.getComponent().getClassName());
    }
    @Test
    public void testSignUpIntent() {
        Intent intent = IntentFactory.getSignUpIntent(context);
        assertEquals(SignUpActivity.class.getName(), intent.getComponent().getClassName());
    }
}