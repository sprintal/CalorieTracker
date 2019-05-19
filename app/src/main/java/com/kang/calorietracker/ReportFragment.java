package com.kang.calorietracker;

import android.app.DatePickerDialog;
import android.app.Fragment;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.kang.calorietracker.helper.DailyReport;
import com.kang.calorietracker.helper.PeriodReport;
import com.kang.calorietracker.helper.Report;
import com.kang.calorietracker.helper.User;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {
    View vReport;
    AlertDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vReport = inflater.inflate(R.layout.fragment_report, container, false);
        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        Gson gson = new Gson();
        final User loginUser = gson.fromJson(userJson, User.class);
        final Calendar date = Calendar.getInstance();
        final EditText dateEdit = vReport.findViewById(R.id.edit_pie_chart);
        final EditText dateBeginEdit = vReport.findViewById(R.id.edit_bar_chart_begin);
        final TextInputLayout dateBeginWrapper = vReport.findViewById(R.id.wrapper_bar_chart_begin);
        final EditText dateEndEdit = vReport.findViewById(R.id.edit_bar_chart_end);
        final TextInputLayout dateEndWrapper = vReport.findViewById(R.id.wrapper_bar_chart_end);
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                dateEdit.setText(sdf.format(date.getTime()));
            }
        };
        final DatePickerDialog.OnDateSetListener dateBeginPicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                dateBeginEdit.setText(sdf.format(date.getTime()));
            }
        };
        final DatePickerDialog.OnDateSetListener dateEndPicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
                dateEndEdit.setText(sdf.format(date.getTime()));
            }
        };
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), datePicker, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dpd.show();
            }
        });

        dateBeginEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), dateBeginPicker, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dpd.show();
            }
        });
        dateEndEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), dateEndPicker, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                DatePicker dp = dpd.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                dpd.show();
            }
        });
        final TextInputLayout dateWrapper = vReport.findViewById(R.id.wrapper_pie_chart);
        dateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                dateWrapper.setError(null);
            }
        });

        final Button pieChartButton = vReport.findViewById(R.id.button_generate_pie);
        pieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateStr = dateEdit.getText().toString().trim();
                if (dateStr.equals("")) {
                    dateWrapper.setError("Please choose a date!");
                }
                else {
                    GetDailyReportsAsyncTask getDailyReportsAsyncTask = new GetDailyReportsAsyncTask();
                    getDailyReportsAsyncTask.execute(String.valueOf(loginUser.id), dateStr);
                }
            }
        });

        final Button barChartButton = vReport.findViewById(R.id.button_generate_bar);
        barChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beginDate = dateBeginEdit.getText().toString().trim();
                String endDate = dateEndEdit.getText().toString().trim();
                if (beginDate.equals("")) {
                    dateBeginWrapper.setError("Please choose a date!");
                }
                if (endDate.equals("")) {
                    dateEndWrapper.setError("Please choose a date!");
                }
                if (!beginDate.equals("") && !endDate.equals("")) {
                    GetPeriodReportAsyncTask getPeriodReportAsyncTask = new GetPeriodReportAsyncTask();
                    getPeriodReportAsyncTask.execute(String.valueOf(loginUser.id), beginDate, endDate);
                }
            }
        });
        return vReport;
    }

    /*
    GET a daily report from server and generate pie chart
     */
    private class GetDailyReportsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String...params) {
            return RestClient.getDailyReport(params[0], params[1]);
        }
        @Override
        protected void onPostExecute (String result) {
            Gson gson = new Gson();
            if (result.equals("")) {
                Toast toast = Toast.makeText(getActivity(), "Can't get report for chosen day!", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            try {
                DailyReport dailyReport = gson.fromJson(result, DailyReport.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_pie_chart);
                dialog = builder.create();
                dialog.show();
                final Button dismissPieButton = dialog.findViewById(R.id.button_dismiss_pie);
                dismissPieButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry((float) dailyReport.totalburned, "Total Burned"));
                entries.add(new PieEntry((float) dailyReport.totalconsumed, "Total Consumed"));

                float remaining = (float) dailyReport.remaining;
                if (remaining < 0) {
                    entries.add(new PieEntry(Math.abs(remaining), "Calorie Deficit"));
                }
                else {
                    entries.add(new PieEntry(Math.abs(remaining), "Calorie Surplus"));
                }
                PieDataSet set = new PieDataSet(entries, "Daily Report");
                set.setColors(new int[] { R.color.yellow, R.color.green, R.color.blue }, getActivity());
                set.setValueTextSize(15f);
                set.setSliceSpace(2f);
                set.setSelectionShift(10f);
                set.setValueFormatter(new PercentFormatter());
                PieData data = new PieData(set);
                PieChart pieChart = dialog.findViewById(R.id.chart_pie);
                pieChart.setData(data);
                Legend legend = pieChart.getLegend();
                legend.setEnabled(true);
                pieChart.setEntryLabelTextSize(20);
                pieChart.setUsePercentValues(true);
                pieChart.setEntryLabelColor(R.color.black);
                pieChart.setUsePercentValues(true);
                pieChart.setHoleRadius(0);
                pieChart.setTransparentCircleRadius(0);
                legend.setFormSize(20f);

            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity(), "Can't get report for chosen day!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /*
    GET a list of reports and generate bar chart
     */
    private class GetPeriodReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String...params) {
            return RestClient.getPeriodReport(params[0], params[1], params[2]);
        }
        @Override
        protected void onPostExecute (String result) {
            if (result.equals("") || result.equals("[]")) {
                Toast toast = Toast.makeText(getActivity(), "Can't get report for chosen period!", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            try {
                Gson gson = new Gson();
                Report[] reports = gson.fromJson(result, Report[].class);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_bar_chart);
                dialog = builder.create();
                dialog.show();
                final Button dismissPieButton = dialog.findViewById(R.id.button_dismiss_bar);
                dismissPieButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                List<BarEntry> entriesGroupBurned = new ArrayList<>();
                List<BarEntry> entriesGroupConsumed = new ArrayList<>();
                final List<String> xAxisLabel = new ArrayList<>();
                for (int i = 0; i < reports.length; i++) {
                    entriesGroupBurned.add(new BarEntry(i, (float) reports[i].burned));
                    entriesGroupConsumed.add(new BarEntry(i, (float) reports[i].consumed));
                    xAxisLabel.add(reports[i].date);

                }
                BarDataSet setBurned = new BarDataSet(entriesGroupBurned, "Burned");
                BarDataSet setConsumed = new BarDataSet(entriesGroupConsumed, "Consumed");
                float groupSpace = 0.1f;
                float barSpace = 0.02f;
                float barWidth = 0.25f;
                setBurned.setValueTextSize(15f);
                setBurned.setColors(new int[] { R.color.green }, getActivity());
                setConsumed.setValueTextSize(15f);
                setConsumed.setColors(new int[] { R.color.blue }, getActivity());
                BarData data = new BarData(setBurned, setConsumed);
                data.setBarWidth(barWidth);
                BarChart barchart = dialog.findViewById(R.id.chart_bar);
                barchart.setData(data);
                barchart.getXAxis().setAxisMinimum(0);
                barchart.getXAxis().setAxisMaximum(0 + barchart.getBarData().getGroupWidth(groupSpace, barSpace) * reports.length);
                barchart.groupBars(0, groupSpace, barSpace);
                XAxis xAxis = barchart.getXAxis();
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return xAxisLabel.get((int) value);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
