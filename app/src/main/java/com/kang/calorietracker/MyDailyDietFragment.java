package com.kang.calorietracker;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kang.calorietracker.helper.Consumption;
import com.kang.calorietracker.helper.Food;
import com.kang.calorietracker.helper.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MyDailyDietFragment extends Fragment {
    View vDailyDiet;
    HashMap<String, List<Food>> categoryFood = new HashMap<>();
    Spinner categorySpinner;
    Spinner foodSpinner;
    Button addDietButton;
    FloatingActionButton refreshButton;
    Set<String> categories;
    ArrayAdapter<Object> categorySpinnerAdapter;
    ArrayAdapter<String> foodSpinnerAdapter;
    List<Food> foods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDailyDiet = inflater.inflate(R.layout.fragment_daily_diet, container, false);
        final TextView foodNameText = vDailyDiet.findViewById(R.id.text_food_name);
        final TextView foodInfoText = vDailyDiet.findViewById(R.id.text_food_info);
        refreshButton = vDailyDiet.findViewById(R.id.button_refresh);
        final Button addFoodButton = vDailyDiet.findViewById(R.id.button_add_new_food);
        addDietButton = vDailyDiet.findViewById(R.id.button_add_diet);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.setEnabled(false);
                GetFoodAsyncTask getFoodAsyncTask = new GetFoodAsyncTask();
                getFoodAsyncTask.execute();
            }
        });

        SharedPreferences user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userJson = user.getString("user", null);
        final Gson gson = new Gson();
        final User loginUser = gson.fromJson(userJson, User.class);

        GetFoodAsyncTask getFoodAsyncTask = new GetFoodAsyncTask();
        getFoodAsyncTask.execute();
        categorySpinner = vDailyDiet.findViewById(R.id.spinner_category);
        foodSpinner = vDailyDiet.findViewById(R.id.spinner_food);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = categorySpinner.getSelectedItem().toString();
                foods = categoryFood.get(category);
                List<String> foodNames = new ArrayList<>();
                for (int i = 0; i < foods.size(); i++) {
                    foodNames.add(foods.get(i).name);
                }
                foodSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, foodNames);
                foodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                foodSpinner.setAdapter(foodSpinnerAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Food food = foods.get(position);
                foodNameText.setText(food.name);
                foodInfoText.setText("Containing " + food.calorieamount + " calories / " + food.servingamount + " "  + food.servingunit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        final EditText consumeAmount = vDailyDiet.findViewById(R.id.edit_diet_amount);
        final TextInputLayout consumeAmountWrapper = vDailyDiet.findViewById(R.id.wrapper_food_amount);

        addDietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consumeAmount.getText().toString().trim().equals("")) {
                    consumeAmountWrapper.setError("Please enter amount!");
                }
                else if (Double.valueOf(consumeAmount.getText().toString().trim()) <= 0) {
                    consumeAmountWrapper.setError("Please enter a positive amount!");
                }
                else {
                    Food food = foods.get(foodSpinner.getSelectedItemPosition());
                    double amount = Double.valueOf(consumeAmount.getText().toString().trim());
                    Consumption consumption = new Consumption();
                    consumption.foodid = food;
                    consumption.quantity = amount;
                    consumption.userid = loginUser;
                    Calendar now = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String nowStr = sdf.format(now.getTime());
                    consumption.date = nowStr;
                    PostConsumptionAsyncTask postConsumptionAsyncTask = new PostConsumptionAsyncTask();
                    postConsumptionAsyncTask.execute(consumption);
                    addDietButton.setEnabled(false);
                    addDietButton.setText("Please Wait");
                }
            }
        });
        return vDailyDiet;
    }

    private class GetFoodAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground (Void...params) {
            return RestClient.getFoodList();
        }

        @Override
        protected void onPostExecute (String result) {
            System.out.println(result);
            Gson gson = new Gson();
            try {
                Food[] foodList = gson.fromJson(result, Food[].class);
                categoryFood = new HashMap<>();
                for (Food item : foodList) {
                    List<Food> foods;
                    if (categoryFood.get(item.category) == null) {
                        foods = new ArrayList<>();
                    } else {
                        foods = categoryFood.get(item.category);
                    }
                    foods.add(item);
                    categoryFood.put(item.category, foods);
                }
                categories = categoryFood.keySet();
                categorySpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories.toArray());
                categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categorySpinnerAdapter);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity(), "Can't get food list from server, please check internet connection and try again!", Toast.LENGTH_LONG);
                toast.show();
            }
            refreshButton.setEnabled(true);
        }
    }

    private class PostConsumptionAsyncTask extends  AsyncTask<Consumption, Void, String> {
        @Override
        protected String doInBackground (Consumption...params) {
            return RestClient.postConsumption(params[0]);
        }

        @Override
        protected void onPostExecute (String result) {
            Toast toast;
            if (result.equals("successful")) {
                toast = Toast.makeText(getActivity(), "Successfully added!", Toast.LENGTH_LONG);
            }
            else {
                toast = Toast.makeText(getActivity(), "Successfully failed, please check internet connection and try again!", Toast.LENGTH_LONG);
            }
            addDietButton.setText(R.string.button_add_diet);
            addDietButton.setEnabled(true);
            toast.show();
        }
    }
}


