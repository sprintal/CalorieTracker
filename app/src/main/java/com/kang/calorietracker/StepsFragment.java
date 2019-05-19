package com.kang.calorietracker;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kang.calorietracker.helper.User;
import com.kang.calorietracker.steps.Steps;
import com.kang.calorietracker.steps.StepsDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StepsFragment extends Fragment {
    View vSteps;
    StepsDatabase db = null;
    ArrayAdapter adapter;
    ListView stepsList;
    List<Steps> steps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSteps = inflater.inflate(R.layout.fragment_steps, container, false);
        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        final User loginUser = gson.fromJson(userJson, User.class);
        db = Room.databaseBuilder(getActivity(), StepsDatabase.class, "StepsDatabase").fallbackToDestructiveMigration().build();
        final EditText stepsEdit = vSteps.findViewById(R.id.edit_steps);
        final TextInputLayout stepsWrapper = vSteps.findViewById(R.id.wrapper_steps);
        stepsList = vSteps.findViewById(R.id.list_steps);
        stepsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                stepsWrapper.setError(null);
            }
        });

        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();
        //Steps steps = new Steps(loginUser.email, "11:11", 1234);
        //InsertDatabase addDatabase = new InsertDatabase();
        //addDatabase.execute(steps);
//        final Button updateButton = vSteps.findViewById(R.id.button_update);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DeleteAllDatabase deleteAllDatabase = new DeleteAllDatabase();
//                deleteAllDatabase.execute();
//            }
//        });

        final Button addStepsButton = vSteps.findViewById(R.id.button_add_steps);
        addStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepsEdit.getText().toString().trim().equals("")) {
                    stepsWrapper.setError("Please enter number of steps!");
                }
                else if (Integer.valueOf(stepsEdit.getText().toString().trim()) <= 0) {
                    stepsWrapper.setError("Please enter a positive number!");
                }
                else {
                    Calendar now = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String nowStr = sdf.format(now.getTime());
                    Steps steps = new Steps(loginUser.email, nowStr, Integer.valueOf(stepsEdit.getText().toString().trim()));
                    InsertDatabase addDatabase = new InsertDatabase();
                    addDatabase.execute(steps);
                }
            }
        });

        stepsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Steps step = steps.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_steps);
                final AlertDialog dialog = builder.create();
                dialog.show();
                final TextView stepsTimeText = dialog.findViewById(R.id.text_steps_time);
                stepsTimeText.setText(step.getTime());
                final EditText stepsChangeEdit = dialog.findViewById(R.id.edit_change_steps);
                final TextInputLayout stepsChangeWrapper = dialog.findViewById(R.id.wrapper_edit_steps);
                final Button saveButton = dialog.findViewById(R.id.button_save_steps_edit);
                final Button cancelButton = dialog.findViewById(R.id.button_cancel_steps_edit);
                stepsChangeEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        stepsChangeWrapper.setError(null);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stepsChangeEdit.getText().toString().trim().equals("")) {
                            stepsChangeWrapper.setError("Please enter number of steps!");
                        }
                        else if (Integer.valueOf(stepsChangeEdit.getText().toString().trim()) <= 0) {
                            stepsChangeWrapper.setError("Please enter a positive number!");
                        }
                        else {
                            step.setSteps(Integer.valueOf(stepsChangeEdit.getText().toString().trim()));
                            UpdateDatabase updateDatabase = new UpdateDatabase();
                            updateDatabase.execute(step);
                            ReadDatabase readDatabase = new ReadDatabase();
                            readDatabase.execute();
                            dialog.dismiss();
                        }

                    }
                });
            }
        });
        return vSteps;
    }

    private class InsertDatabase extends AsyncTask<Steps, Void, Long> {
        @Override
        protected Long doInBackground(Steps... params) {
            long id = db.stepsDao().insert(params[0]);
            return id;
        }

        @Override
        protected void onPostExecute(Long details) {
            System.out.println("id : " + details);
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
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
            Toast toast = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
            toast.show();
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
        }
    }

    /*
    Read all from database and display in list
     */
    private class ReadDatabase extends AsyncTask<Void, Void, List<Steps>> {
        @Override
        protected List<Steps> doInBackground(Void... params) {
            steps = db.stepsDao().getAll();
            return steps;
        }
        @Override
        protected void onPostExecute(List<Steps> result) {
            List<String> displayList = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                displayList.add(result.get(i).getTime() + "\n" + result.get(i).getSteps());
            }
            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, displayList);
            stepsList.setAdapter(adapter);
        }
    }

    /*
    Update database, add new steps entry
     */
    private class UpdateDatabase extends AsyncTask<Steps, Void, String> {
        @Override
        protected String doInBackground(Steps...params) {
            db.stepsDao().update(params[0]);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            return;
        }
    }

}
