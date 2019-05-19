package com.kang.calorietracker;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kang.calorietracker.data.DailyGoal;
import com.kang.calorietracker.helper.BurnedPerStep;
import com.kang.calorietracker.helper.TotalBurnedAtRest;
import com.kang.calorietracker.helper.TotalConsumed;
import com.kang.calorietracker.helper.User;
import com.kang.calorietracker.steps.Steps;
import com.kang.calorietracker.steps.StepsDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CalorieTrackerFragment extends Fragment {
    View vCalorieTracker;
    StepsDatabase db = null;
    int goal;
    TextView goalText;
    TextView stepsText;
    TextView consumedText;
    TextView burnedText;
    double totalBurned = 0;
    int totalSteps = 0;
    User loginUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vCalorieTracker = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);
        goalText = vCalorieTracker.findViewById(R.id.text_tracker_goal);
        stepsText = vCalorieTracker.findViewById(R.id.text_tracker_steps);
        consumedText = vCalorieTracker.findViewById(R.id.text_tracker_consumed);
        burnedText = vCalorieTracker.findViewById(R.id.text_tracker_burned);
        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        loginUser = gson.fromJson(userJson, User.class);
        db = Room.databaseBuilder(getActivity(), StepsDatabase.class, "StepsDatabase").fallbackToDestructiveMigration().build();
        final DailyGoal dailyGoal = (DailyGoal) getActivity().getApplication();
        HashMap goals = dailyGoal.getUserGoals();
        Calendar date = dailyGoal.getDate();
        Calendar now = Calendar.getInstance();
        if (date.get(Calendar.YEAR) != now.get(Calendar.YEAR) || date.get(Calendar.MONTH) != now.get(Calendar.MONTH) || date.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
            goal = 0;
        }
        else if (goals.get(loginUser.email) == null) {
            goal = 0;
        }
        else {
            goal = (Integer) goals.get(loginUser.email);
        }

        String goalStr = "";
        if (goal < 0) {
            goalStr = "Lose " + Math.abs(goal) + " Calories";
        }
        else {
            goalStr = "Gain " + goal + " Calories";
        }

        goalText.setText(goalStr);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowStr = sdf.format(now.getTime());

        GetDatabaseAsyncTask getDatabaseAsyncTask = new GetDatabaseAsyncTask();
        getDatabaseAsyncTask.execute(loginUser.email);

        GetTotalConsumedAsyncTask getTotalConsumedAsyncTask = new GetTotalConsumedAsyncTask();
        getTotalConsumedAsyncTask.execute(String.valueOf(loginUser.id), nowStr);

        return vCalorieTracker;
    }

    private class GetDatabaseAsyncTask extends AsyncTask<String, Void, List<Steps>> {
        @Override
        protected List<Steps> doInBackground(String... params) {
            List<Steps> steps = db.stepsDao().findByEmail(params[0]);
            return steps;
        }
        @Override
        protected void onPostExecute(List<Steps> result) {
            for (int i = 0; i < result.size(); i++) {
                totalSteps += result.get(i).getSteps();
                String totalStepsStr = totalSteps + " Steps";
                stepsText.setText(totalStepsStr);
            }
            GetBurnedPerStepAsyncTask getBurnedPerStepAsyncTask = new GetBurnedPerStepAsyncTask();
            getBurnedPerStepAsyncTask.execute(String.valueOf(loginUser.id));

        }
    }

    private class GetTotalConsumedAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = RestClient.getTotalConsumed(params[0], params[1]);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            try {
                TotalConsumed totalConsumed = gson.fromJson(result, TotalConsumed.class);
                String totalConsumedStr = totalConsumed.totalconsumed + " Calories";
                consumedText.setText(totalConsumedStr);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class GetBurnedPerStepAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = RestClient.getBurnedPerStep(params[0]);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            try {
                BurnedPerStep burnedPerStep = gson.fromJson(result, BurnedPerStep.class);
                totalBurned += burnedPerStep.burnedperstep * totalSteps;
                System.out.println("burnedPerStep: " + burnedPerStep.burnedperstep);
                System.out.println("totalBurned: " + totalBurned);
                GetTotalBurnedAtRestAsyncTask getTotalBurnedAtRestAsyncTask = new GetTotalBurnedAtRestAsyncTask();
                getTotalBurnedAtRestAsyncTask.execute(String.valueOf(loginUser.id));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class GetTotalBurnedAtRestAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getBurnedAtRest(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            try {
                TotalBurnedAtRest totalBurnedAtRest = gson.fromJson(result, TotalBurnedAtRest.class);
                totalBurned += totalBurnedAtRest.totalburnedatrest;
                System.out.println("totalBurnedAtRest: " + totalBurnedAtRest.totalburnedatrest);
                System.out.println("totalBurned: " + totalBurned);
                String totalBurnedStr = totalBurned + " Calories";
                burnedText.setText(totalBurnedStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
