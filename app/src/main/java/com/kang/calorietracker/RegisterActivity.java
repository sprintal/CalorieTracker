package com.kang.calorietracker;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        List<Integer> levelOfActivityList = new ArrayList<Integer>();
        levelOfActivityList.add(1);
        levelOfActivityList.add(2);
        levelOfActivityList.add(3);
        levelOfActivityList.add(4);
        levelOfActivityList.add(5);

        final Spinner levelOfActivitySpinner = findViewById(R.id.spinner_level_of_activity);
        final ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelOfActivityList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelOfActivitySpinner.setAdapter(spinnerAdapter);

        final Calendar dobPicker = Calendar.getInstance();
        final EditText dobRegister = findViewById(R.id.register_dob);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dobPicker.set(Calendar.YEAR, year);
                dobPicker.set(Calendar.MONTH, month);
                dobPicker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                dobRegister.setText(sdf.format(dobPicker.getTime()));
            }
        };

        dobRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(RegisterActivity.this, date, dobPicker.get(Calendar.YEAR), dobPicker.get(Calendar.MONTH), dobPicker.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dpd.show();
            }
        });

    }

    private void onRadioButtonClicked() {

    }

}
