package com.kang.calorietracker;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.RegisterStatus;
import helper.User;

public class RegisterActivity extends AppCompatActivity {
    private User user;
    private static final String URL_STR = "http://localhost:8080/CalorieTrackerServer/webresources/restws.credential/register/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Registration");
        user = new User();
        setContentView(R.layout.activity_register);

        List<Integer> levelOfActivityList = new ArrayList<>();
        levelOfActivityList.add(0);
        levelOfActivityList.add(1);
        levelOfActivityList.add(2);
        levelOfActivityList.add(3);
        levelOfActivityList.add(4);
        levelOfActivityList.add(5);

        final TextView spinnerWarn = findViewById(R.id.warn_spinner);
        final Spinner levelOfActivitySpinner = findViewById(R.id.spinner_level_of_activity);
        final ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelOfActivityList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelOfActivitySpinner.setAdapter(spinnerAdapter);
        levelOfActivitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerWarn.setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        final Calendar dobPicker = Calendar.getInstance();
        final EditText DoBEdit = findViewById(R.id.register_dob);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dobPicker.set(Calendar.YEAR, year);
                dobPicker.set(Calendar.MONTH, month);
                dobPicker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                DoBEdit.setText(sdf.format(dobPicker.getTime()));
            }
        };

        DoBEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(RegisterActivity.this, date, dobPicker.get(Calendar.YEAR), dobPicker.get(Calendar.MONTH), dobPicker.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dpd.show();
            }
        });

        final EditText firstNameEdit = findViewById(R.id.register_first_name);
        final TextInputLayout firstNameWrapper = findViewById(R.id.wrapper_first_name);
        firstNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                    firstNameWrapper.setError(null);
            }
        });

        final EditText surnameEdit = findViewById(R.id.register_surname);
        final TextInputLayout surnameWrapper = findViewById(R.id.wrapper_surname);
        surnameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                surnameWrapper.setError(null);
            }
        });

        final EditText emailEdit = findViewById(R.id.register_email);
        final TextInputLayout emailWrapper = findViewById(R.id.wrapper_email);
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                emailWrapper.setError(null);
            }
        });


        final TextInputLayout DoBWrapper = findViewById(R.id.wrapper_dob);
        DoBEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                DoBWrapper.setError(null);
            }
        });

        final EditText heightEdit = findViewById(R.id.register_height);
        final TextInputLayout heightWrapper = findViewById(R.id.wrapper_height);
        heightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                heightWrapper.setError(null);
            }
        });

        final EditText weightEdit = findViewById(R.id.register_weight);
        final TextInputLayout weightWrapper = findViewById(R.id.wrapper_weight);
        weightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                weightWrapper.setError(null);
            }
        });

        final EditText addressEdit = findViewById(R.id.register_address);
        final TextInputLayout addressWrapper = findViewById(R.id.wrapper_address);
        addressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                addressWrapper.setError(null);
            }
        });

        final EditText postcodeEdit = findViewById(R.id.register_postcode);
        final TextInputLayout postcodeWrapper = findViewById(R.id.wrapper_postcode);
        postcodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                postcodeWrapper.setError(null);
            }
        });

        final EditText stepsPerMileEdit = findViewById(R.id.register_steps_per_mile);
        final TextInputLayout stepsPerMileWrapper = findViewById(R.id.wrapper_steps_per_mile);
        stepsPerMileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                stepsPerMileWrapper.setError(null);
            }
        });

        final EditText usernameEdit = findViewById(R.id.register_username);
        final TextInputLayout usernameWrapper = findViewById(R.id.wrapper_username);
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                usernameWrapper.setError(null);
            }
        });

        final EditText passwordEdit = findViewById(R.id.register_password);
        final TextInputLayout passwordWrapper = findViewById(R.id.wrapper_password);
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                passwordWrapper.setError(null);
            }
        });

        final EditText repeatPasswordEdit = findViewById(R.id.register_repeat_password);
        final TextInputLayout repeatPasswordWrapper = findViewById(R.id.wrapper_repeat_password);
        repeatPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                repeatPasswordWrapper.setError(null);
            }
        });

        final Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = 0;
                final TextView genderWarn = findViewById(R.id.warn_gender);
                if (user.getUserid().getGender() == "") {
                    genderWarn.setText("Please choose a gender!");
                }
                else {
                    genderWarn.setText("");
                }

                String firstName = firstNameEdit.getText().toString().trim();
                if (firstName.equals("")) {
                    firstNameWrapper.setError("Please enter your first name!");
                }
                else {
                    user.getUserid().setName(firstName);
                    checked += 1;
                }


                String surname = surnameEdit.getText().toString().trim();
                if (surname.equals("")) {
                    surnameWrapper.setError("Please enter your surname!");
                }
                else {
                    user.getUserid().setSurname(surname);
                    checked += 1;
                }

                // Regex reference: https://stackoverflow.com/a/8204716
                String email = emailEdit.getText().toString().trim().toLowerCase();
                Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                Matcher emailMatcher = emailPattern.matcher(email);
                if (email.equals("")) {
                    emailWrapper.setError("Please enter your email address!");
                }
                else if (!emailMatcher.find()) {
                    emailWrapper.setError("Please enter a valid email address!");
                }
                else {
                    user.getUserid().setEmail(email);
                    checked += 1;
                }

                String DoB = DoBEdit.getText().toString().trim();
                if (DoB.equals("")) {
                    DoBWrapper.setError("Please enter your date of birth!");
                }
                else {
                    user.getUserid().setDob(DoB);
                    checked += 1;
                }

                String heightStr = heightEdit.getText().toString().trim();
                if (heightStr.equals("")) {
                    heightWrapper.setError("Please enter your height!");
                }
                else {
                    try {
                        double height = Double.parseDouble(heightStr);
                        user.getUserid().setHeight(height);
                        checked += 1;
                    } catch (Exception e) {
                        heightWrapper.setError("Please enter a valid height!");
                    }
                }

                String weightStr = weightEdit.getText().toString().trim();
                if (weightStr.equals("")) {
                    weightWrapper.setError("Please enter your weight!");
                }
                else {
                    try {
                        double weight = Double.parseDouble(weightStr);
                        user.getUserid().setWeight(weight);
                        checked += 1;
                    } catch (Exception e) {
                        weightWrapper.setError("Please enter a valid weight!");
                    }

                }

                String address = addressEdit.getText().toString().trim();
                if (address.equals("")) {
                    addressWrapper.setError("Please enter your address!");
                }
                else {
                    user.getUserid().setAddress(address);
                    checked += 1;
                }

                String postcode = postcodeEdit.getText().toString().trim();
                if (postcode.equals("")) {
                    postcodeWrapper.setError("Please enter your postcode");
                }
                else {
                    user.getUserid().setPostcode(postcode);
                    checked += 1;
                }

                String stepsPerMileStr = stepsPerMileEdit.getText().toString().trim();
                if (stepsPerMileStr.equals("")) {
                    stepsPerMileWrapper.setError("Please enter your number of steps per mile!");
                }
                else {
                    try {
                        int stepsPerMile = Integer.parseInt(stepsPerMileStr);
                        user.getUserid().setStepspermile(stepsPerMile);
                        checked += 1;
                    } catch (Exception e) {
                        stepsPerMileWrapper.setError("Please enter a valid number!");
                    }
                }

                String username = usernameEdit.getText().toString().trim();
                if (username.equals("")) {
                    usernameWrapper.setError("Please enter your username!");
                }
                else {
                    user.setUsername(username);
                    checked += 1;
                }

                String password = passwordEdit.getText().toString().trim();
                if (password.equals("")) {
                    passwordWrapper.setError("Please enter your password!");
                }
                else if (password.length() < 8) {
                    passwordWrapper.setError("Your password is too short, try something longer than 8");
                }
                else {
                    user.setPasswordhash(password);
                    checked += 1;
                }

                String repeatPassword = repeatPasswordEdit.getText().toString().trim();
                if (repeatPassword.equals("")) {
                    repeatPasswordWrapper.setError("Please enter your password again!");
                }
                else if (!repeatPassword.equals(password)) {
                    repeatPasswordWrapper.setError("Your password doesn't match!");
                }
                else {
                    checked += 1;
                }

                if (user.getUserid().getGender().equals("Male") || user.getUserid().getGender().equals("Female")) {
                    checked += 1;
                }

                String levelOfActivityStr = levelOfActivitySpinner.getSelectedItem().toString();
                int levelOfActivity = Integer.parseInt(levelOfActivityStr);
                if (levelOfActivity > 0 && levelOfActivity < 6) {
                    user.getUserid().setLevelofactivity(levelOfActivity);
                    checked += 1;
                }
                else {
                    spinnerWarn.setText("Please choose!");
                }

                if (checked == 14) {
                    System.out.println("Ready to fire");
                    registerButton.setText("Please wait");
                    registerButton.setEnabled(false);
//                    new Handler().postDelayed(new Runnable() {
//                        public void run() {
//                            Toast toast = Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_LONG);
//                            toast.show();
//
//                        }
//                    }, 2000);
//                    finish();
                    PostAsyncTask postAsyncTask = new PostAsyncTask();
                    postAsyncTask.execute(user);

                }
            }
        });


    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    user.getUserid().setGender("Male");
                break;
            case R.id.radio_female:
                if (checked)
                    user.getUserid().setGender("Female");
                break;
        }
        final TextView warnGender = findViewById(R.id.warn_gender);
        warnGender.setText(null);
    }

    private class PostAsyncTask extends AsyncTask<User, Void, String> {
        @Override
        protected String doInBackground(User... params) {
            String result = RestClient.register(params[0]);
            System.out.println("In doinbackground");
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            Button rButton = findViewById(R.id.register);

            Gson gson = new Gson();
            if (result.equals("")) {
                final Toast toast = Toast.makeText(RegisterActivity.this, "Server can't be reached, please check network status!", Toast.LENGTH_LONG);
                toast.show();
                rButton.setEnabled(true);
                return;
            }

            RegisterStatus registerStatus = gson.fromJson(result, RegisterStatus.class);
            if (registerStatus.getStatus().equals("successful")) {
                final Toast toast = Toast.makeText(RegisterActivity.this, "Registration successful, please login!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
            else {
                if (registerStatus.getInfo().getUsername().equals("exist")) {
                    TextInputLayout usernameWrapper = findViewById(R.id.wrapper_username);
                    usernameWrapper.setError("Username exist, try another one!");
                    }
                if (registerStatus.getInfo().getEmail().equals("exist")) {
                    TextInputLayout emailWrapper = findViewById(R.id.wrapper_email);
                    emailWrapper.setError("Email exist, try another one!");
                }

                rButton.setEnabled(true);
            }
        }
    }


}
