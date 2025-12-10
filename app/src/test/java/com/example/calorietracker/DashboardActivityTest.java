package com.example.calorietracker;

import static org.junit.Assert.*;
import org.junit.Test;

public class DashboardActivityTest {

    @Test
    public void testRetrieveCalories_validEntry() {
        String entry = "Banana: 105 cal";
        int result = DashboardActivity.retrieveCalories(entry);
        assertEquals(105, result);
    }

    @Test
    public void testRetrieveCalories_invalidEntry() {
        String entry = "Banana: invalid cal";
        int result = DashboardActivity.retrieveCalories(entry);
        assertEquals(-1, result);
    }

    @Test
    public void testGetComparisonLess() {
        int calories = 2;
        int target = 3;
        String result = DashboardActivity.getComparison(calories, target);
        assertEquals("<", result);
    }

    @Test
    public void testGetComparisonGreater() {
        int calories = 4;
        int target = 3;
        String result = DashboardActivity.getComparison(calories, target);
        assertEquals(">", result);
    }

    @Test
    public void testGetComparisonEqual() {
        int calories = 3;
        int target = 3;
        String result = DashboardActivity.getComparison(calories, target);
        assertEquals("=", result);
    }

}
