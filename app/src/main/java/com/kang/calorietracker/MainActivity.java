package com.kang.calorietracker;

import android.app.AlarmManager;
import android.app.Fragment;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.TimeZone;

/*
Main Activity, managing fragments
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean loginStatus;
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private Report report = new Report();
    double totalBurned = 0;
    User loginUser;
    Integer goal = 0;
    StepsDatabase db = null;
    int totalSteps = 0;
    String nowStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        loginStatus = false;

        try {
            loginStatus = getIntent().getBooleanExtra("loginStatus", false);
        } catch (Exception e) {

        }
        if(!loginStatus) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getApplicationContext(), StepsDatabase.class, "StepsDatabase").fallbackToDestructiveMigration().build();
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        loginUser = gson.fromJson(userJson, User.class);
        // Set daily auto update
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        long systemTime = System.currentTimeMillis();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+10"));
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        long selectTime = mCalendar.getTimeInMillis();
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, ScheduledIntentService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),1000 * 60 * 60 * 24, pendingIntent);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Dashboard");


        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new
                DashboardFragment()).commit();

        final Button logoutButton = findViewById(R.id.nav_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        final Button uploadButton = findViewById(R.id.nav_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.userid = loginUser;
                Calendar now = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String nowStr = sdf.format(now.getTime());
                final DailyGoal dailyGoal = (DailyGoal) getApplication();
                HashMap goals = dailyGoal.getUserGoals();
                Calendar date = dailyGoal.getDate();
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
                report.date = nowStr;
                GetDatabaseAsyncTask getDatabaseAsyncTask = new GetDatabaseAsyncTask();
                getDatabaseAsyncTask.execute(loginUser.email);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Routing between fragments
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_dashboard:
                getSupportActionBar().setTitle("Dashboard");
                nextFragment = new DashboardFragment();
                break;
            case R.id.nav_daily_diet:
                getSupportActionBar().setTitle("My Daily Diet");
                nextFragment = new MyDailyDietFragment();
                break;
            case R.id.nav_steps:
                getSupportActionBar().setTitle("Steps");
                nextFragment = new StepsFragment();
                break;
            case R.id.nav_calorie_tracker:
                getSupportActionBar().setTitle("Calorie Tracker");
                nextFragment = new CalorieTrackerFragment();
                break;
            case R.id.nav_report:
                getSupportActionBar().setTitle("Report");
                nextFragment = new ReportFragment();
                break;
            case R.id.nav_map:
                getSupportActionBar().setTitle("Map");
                nextFragment = new MapFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if(nextFragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Go back to Login Activity
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Really?");
        builder.setMessage("You really want to leave us?");

        builder.setCancelable(true);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast toast = Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });
        builder.setPositiveButton("HELL NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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
