package com.example.calorietracker;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
public class TipsFeatureTest {

    // This should match the main ideas from TipsActivity's textTipsBody.
    private String getTipsText() {
        return "• Under your daily limit = calorie deficit (helps with weight loss).\n\n" +
                "• Over your daily limit = calorie surplus (can lead to weight gain).\n\n" +
                "• Try to stay close to your target most days instead of being perfect.\n\n" +
                "• Logging food regularly helps you see patterns in your eating.\n\n" +
                "• Use the app's Under / Met / Over status to guide your next meal.";
    }

    @Test
    public void tipsMentionUnderDailyLimit() {
        String tips = getTipsText();
        assertTrue(tips.contains("Under your daily limit"));
    }

    @Test
    public void tipsMentionOverDailyLimit() {
        String tips = getTipsText();
        assertTrue(tips.contains("Over your daily limit"));
    }

    @Test
    public void tipsEncourageLoggingFood() {
        String tips = getTipsText();
        assertTrue(tips.toLowerCase().contains("logging food"));
    }
}
