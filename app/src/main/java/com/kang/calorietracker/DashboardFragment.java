package com.kang.calorietracker;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.kang.calorietracker.data.DailyGoal;
import com.kang.calorietracker.helper.Credential;
import com.kang.calorietracker.helper.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {
    View vDashboard;
    HashMap goals;
    Calendar now;
    Integer goal = 0;
    String choice = "GAIN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDashboard = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //final String username = "Kang";
        final DailyGoal dailyGoal = (DailyGoal) getActivity().getApplication();
        //final LoginUser loginCredential = (LoginUser) getActivity().getApplication();
        final TextView welcomeText = vDashboard.findViewById(R.id.text_welcome);
        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        final User loginUser = gson.fromJson(userJson, User.class);
        try {
            welcomeText.setText("Welcome, " + loginUser.getName() + "!");

        goals = dailyGoal.getUserGoals();
        Calendar date = dailyGoal.getDate();
        now = Calendar.getInstance();
        if (date.get(Calendar.YEAR) != now.get(Calendar.YEAR) || date.get(Calendar.MONTH) != now.get(Calendar.MONTH) || date.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
            goal = 0;
        }
        else if (goals.get(loginUser.email) == null) {
            goal = 0;
        }
        else {
            goal = (Integer) goals.get(loginUser.email);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final TextClock clock = vDashboard.findViewById(R.id.clock);
        clock.setFormat24Hour("yyyy-MM-dd hh:mm:ss aa");
        final Button goalButton = vDashboard.findViewById(R.id.button_goal);
        final TextView goalText = vDashboard.findViewById(R.id.text_goal);
        goalText.setText(String.valueOf(goal));
        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_goal);

                List<String> options = new ArrayList<>();
                options.add("LOSE");
                options.add("GAIN");
                final AlertDialog dialog = builder.create();
                dialog.show();
                final Spinner goalSpinner = dialog.findViewById(R.id.spinner_goal);
                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, options);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                goalSpinner.setAdapter(spinnerAdapter);

                final Button setGoal = dialog.findViewById(R.id.button_set_goal);
                final EditText setGoalEdit = dialog.findViewById(R.id.edit_goal);
                final TextInputLayout setGoalWrapper = dialog.findViewById(R.id.wrapper_goal);
                setGoalEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        setGoalWrapper.setError(null);
                    }
                });
                setGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choice = goalSpinner.getSelectedItem().toString();
                        String goalStr = "";
                        try {
                            goalStr = setGoalEdit.getText().toString().trim();
                            goal = Integer.parseInt(goalStr);
                        } catch (Exception e) {
                            setGoalWrapper.setError("Please enter a valid goal!");
                            return;
                        }

                        if (goal < 0) {
                            setGoalWrapper.setError("Please enter a valid goal!");
                            return;
                        }
                        if (choice.equals("GAIN")) {
                            goal = Math.abs(goal);
                        }
                        else if (choice.equals("LOSE")) {
                            goal = 0 - Math.abs(goal);
                        }
                        now =Calendar.getInstance();
                        goalText.setText(String.valueOf(goal));
                        goals = dailyGoal.getUserGoals();
                        goals.put(loginUser.email, goal);
                        dailyGoal.setUserGoals(goals);
                        dailyGoal.setDate(now);
                        dialog.dismiss();
                    }
                });
            }

        });
        return vDashboard;
    }

}
