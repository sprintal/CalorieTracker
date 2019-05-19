package com.kang.calorietracker;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.kang.calorietracker.data.DailyGoal;
import com.kang.calorietracker.helper.BurnedPerStep;
import com.kang.calorietracker.helper.Report;
import com.kang.calorietracker.helper.TotalBurnedAtRest;
import com.kang.calorietracker.helper.TotalConsumed;
import com.kang.calorietracker.helper.User;
import com.kang.calorietracker.steps.Steps;
import com.kang.calorietracker.steps.StepsDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ScheduledIntentService extends IntentService {
    HashMap goals;
    int totalSteps;
    StepsDatabase db = null;
    User loginUser;
    Calendar now;
    Integer goal = 0;
    Report report;
    double totalBurned = 0;
    String nowStr;
    public ScheduledIntentService() {
        super("ScheduledIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        final DailyGoal dailyGoal = (DailyGoal) getApplication();
        goals = dailyGoal.getUserGoals();
        Calendar date = dailyGoal.getDate();
        now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        nowStr = sdf.format(now.getTime());
        if (date.get(Calendar.YEAR) != now.get(Calendar.YEAR) || date.get(Calendar.MONTH) != now.get(Calendar.MONTH) || date.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
            goal = 0;
        }
        else if (goals.get(loginUser.email) == null) {
            goal = 0;
        }
        else {
            goal = (Integer) goals.get(loginUser.email);
        }
        report.goal = goal;
        SharedPreferences user = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        loginUser = gson.fromJson(userJson, User.class);
        db = Room.databaseBuilder(getApplicationContext(), StepsDatabase.class, "StepsDatabase").fallbackToDestructiveMigration().build();
        report.date = nowStr;
        report.userid = loginUser;
        GetDatabaseAsyncTask getDatabaseAsyncTask = new GetDatabaseAsyncTask();
        getDatabaseAsyncTask.execute(loginUser.email);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
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
            }
            report.steps= totalSteps;
            GetTotalConsumedAsyncTask getTotalConsumedAsyncTask = new GetTotalConsumedAsyncTask();
            getTotalConsumedAsyncTask.execute(String.valueOf(loginUser.id), nowStr);

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
                report.consumed = totalConsumed.totalconsumed;
                GetBurnedPerStepAsyncTask getBurnedPerStepAsyncTask = new GetBurnedPerStepAsyncTask();
                getBurnedPerStepAsyncTask.execute(String.valueOf(loginUser.id));
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
                    report.burned = totalBurned;
                    String json = gson.toJson(report);
                    PostReportAsyncTask postReportAsyncTask = new PostReportAsyncTask();
                    postReportAsyncTask.execute(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private class PostReportAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                return RestClient.postReport(params[0]);
            }
            @Override
            protected void onPostExecute(String result) {
                if(result.equals("")) {
                }
                else {
                    DeleteAllDatabase deleteAllDatabase = new DeleteAllDatabase();
                    deleteAllDatabase.execute();
                }
            }
        }

        private class DeleteAllDatabase extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void...params) {
                String result;
                try {
                    db.stepsDao().deleteAll();
                    result = "Deleted";
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "Failed";
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {

            }
        }
    }
}
