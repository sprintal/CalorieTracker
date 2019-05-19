package com.kang.calorietracker.data;

import android.app.Application;

import java.util.Calendar;
import java.util.HashMap;

public class DailyGoal extends Application {
    private Calendar date;
    private HashMap<String, Integer> userGoals;



    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }


    public HashMap<String, Integer> getUserGoals() {
        return userGoals;
    }

    public void setUserGoals(HashMap<String, Integer> userGoals) {
        this.userGoals = userGoals;
    }

    @Override
    public void onCreate() {
        date = Calendar.getInstance();
        userGoals = new HashMap<>();
        super.onCreate();
    }
}
