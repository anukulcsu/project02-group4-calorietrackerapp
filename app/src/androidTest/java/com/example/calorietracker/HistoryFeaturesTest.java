package com.example.calorietracker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HistoryFeaturesTest {
    private String calculateStatus(int totalCalories, int limit) {
        if (totalCalories < limit) {
            return "Under";
        } else if (totalCalories == limit) {
            return "Met";
        } else {
            return "Over";
        }
    }

    @Test
    public void status_isUnder_whenTotalLessThanLimit() {
        String status = calculateStatus(1500, 2000);
        assertEquals("Under", status);
    }

    @Test
    public void status_isMet_whenTotalEqualsLimit() {
        String status = calculateStatus(2000, 2000);
        assertEquals("Met", status);
    }

    @Test
    public void status_isOver_whenTotalGreaterThanLimit() {
        String status = calculateStatus(2200, 2000);
        assertEquals("Over", status);
    }
}
