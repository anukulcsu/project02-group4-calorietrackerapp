package com.example.calorietracker;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class HistoryFeaturesTest {
    private String calculateStatus(int totalCalories) {
        if (totalCalories < 2000) {
            return "Under";
        } else if (totalCalories == 2000) {
            return "Met";
        } else {
            return "Over";
        }
    }
    @Test
    public void status_isUnder_whenTotalLessThanLimit() {
        String status = calculateStatus(1500);
        assertEquals("Under", status);
    }
    @Test
    public void status_isMet_whenTotalEqualsLimit() {
        String status = calculateStatus(2000);
        assertEquals("Met", status);
    }
    @Test
    public void status_isOver_whenTotalGreaterThanLimit() {
        String status = calculateStatus(2200);
        assertEquals("Over", status);
    }
}
