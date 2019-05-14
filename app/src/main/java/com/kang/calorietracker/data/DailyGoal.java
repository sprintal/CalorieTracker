package com.kang.calorietracker.data;

import android.app.Application;

import java.util.Calendar;

public class DailyGoal extends Application {
    private Calendar date;
    private int goal;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    @Override
    public void onCreate() {
        date = Calendar.getInstance();
        goal = 0;
        super.onCreate();
    }
}
