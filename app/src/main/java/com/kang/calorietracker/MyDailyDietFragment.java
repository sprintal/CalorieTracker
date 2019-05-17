package com.kang.calorietracker;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import helper.Food;
import helper.FoodSearchList;
import helper.FoodSearchListEdamam;
import helper.GoogleSearchResult;


public class MyDailyDietFragment extends Fragment {
    View vDailyDiet;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> list = new ArrayList<>();
    ArrayAdapter adapter;
//    FoodSearchList foodSearchList;
    FoodSearchListEdamam foodSearchListEdamam;
    FoodSearchListEdamam.Hints[] foodList;
    FoodSearchListEdamam.Hints.Food food;
    TextView des;
    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDailyDiet = inflater.inflate(R.layout.fragment_daily_diet, container, false);

//        swipeRefreshLayout = vDailyDiet.findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setProgressViewOffset(true, 0, 100);
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
//        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
//                getResources().getColor(android.R.color.holo_red_dark),
//                getResources().getColor(android.R.color.holo_blue_dark),
//                getResources().getColor(android.R.color.holo_orange_dark));
//        ListView mListView = vDailyDiet.findViewById(R.id.list_view);
//
//        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData());
//        mListView.setAdapter(adapter);
        final Button dietButton = vDailyDiet.findViewById(R.id.button_diet);
        final EditText dietEdit = vDailyDiet.findViewById(R.id.edit_diet);
        final TextInputLayout dietWrapper = vDailyDiet.findViewById(R.id.wrapper_diet);
        final ListView foodSearchResultList = vDailyDiet.findViewById(R.id.list_diet);
        dietEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                dietWrapper.setError(null);
            }
        });

        foodSearchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                food = foodSearchListEdamam.hints[position].food;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_food);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Food foodObj = new Food();
                        foodObj.name = food.label;
                        foodObj.category = food.category;
                        foodObj.calorieamount = food.nutrients.ENERC_KCAL;
                        foodObj.fat = food.nutrients.FAT;
                        foodObj.servingamount = 100;
                        foodObj.servingunit = "grams";
                        PostAsyncTask postAsyncTask = new PostAsyncTask();
                        postAsyncTask.execute(foodObj);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                GoogleSearchAsyncTask googleSearchAsyncTask = new GoogleSearchAsyncTask();
                googleSearchAsyncTask.execute(food.label);
                final TextView foodName = dialog.findViewById(R.id.food_name);
                final TextView foodCategory = dialog.findViewById(R.id.food_category);
                final TextView foodEnergy = dialog.findViewById(R.id.food_energy);
                final TextView food_fat = dialog.findViewById(R.id.food_fat);
                final TextView food_description = dialog.findViewById(R.id.food_description);
                final ImageView food_image = dialog.findViewById(R.id.food_image);
                des = food_description;
                img = food_image;
                foodName.setText(food.label);
                foodCategory.setText(food.category);
                foodEnergy.setText(String.valueOf(food.nutrients.ENERC_KCAL));
                food_fat.setText(String.valueOf(food.nutrients.FAT));
                //food_description.setText(description);

            }
        });
        dietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = dietEdit.getText().toString().trim();
                if (query.equals("")) {
                    dietWrapper.setError("Please enter something!");
                }
                else {
                    FoodSearchAsyncTask foodSearchAsyncTask = new FoodSearchAsyncTask();
                    foodSearchAsyncTask.execute(query);
                }
                System.out.println("query: " + query);
            }
        });



        return vDailyDiet;
    }

    private class FoodSearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String...params) {
            return RestClient.searchFood(params[0]);
        }

        @Override
        protected void onPostExecute (String result){
            // TextView resultTextView = vDailyDiet.findViewById(R.id.text_diet);
            ListView resultList = vDailyDiet.findViewById((R.id.list_diet));
            Gson gson = new Gson();
            foodSearchListEdamam = gson.fromJson(result, FoodSearchListEdamam.class);
                if (foodSearchListEdamam.hints.length == 0) {
                Toast toast = Toast.makeText(getActivity(), "No result found, please try another keyword", Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            System.out.println(foodSearchListEdamam.hints[0].food.category);
//            resultTextView.setText(result);
            foodList = foodSearchListEdamam.hints;
            List<String> aList = new ArrayList<>();
            for (FoodSearchListEdamam.Hints item:foodList) {
                aList.add(item.food.label + "\nCategory: " + item.food.category);
            }
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, aList);
            resultList.setAdapter(adapter);
        }

    }

    private class PostAsyncTask extends AsyncTask<Food, Void, String> {
        @Override
        protected String doInBackground(Food...params) {
            String result = RestClient.addFood(params[0]);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private class GoogleSearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...params) {
            String result = RestClient.googleSearch(params[0]);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
            Gson gson = new Gson();
            GoogleSearchResult googleSearchResult = gson.fromJson(result, GoogleSearchResult.class);
            final TextView foodDescription = getView().findViewById(R.id.food_description);
            String description = "";
            String imageLink = "";
                    description = googleSearchResult.items[0].pagemap.product[0].description;
                    imageLink = googleSearchResult.items[0].pagemap.product[0].image;
            if (!imageLink.equals("")) {
                DownloadImageTask downloadImageTask = new DownloadImageTask(img);
                downloadImageTask.execute(imageLink);
            }
            des.setText(description);
            } catch (Exception e) { }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}


