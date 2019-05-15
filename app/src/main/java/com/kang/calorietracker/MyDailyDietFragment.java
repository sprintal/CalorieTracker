package com.kang.calorietracker;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import helper.FoodSearchList;


public class MyDailyDietFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View vDailyDiet;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> list = new ArrayList<>();
    ArrayAdapter adapter;

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
        Button dietButton = vDailyDiet.findViewById(R.id.button_diet);
        TextView dietText = vDailyDiet.findViewById(R.id.text_diet);
        dietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodSearchAsyncTask foodSearchAsyncTask = new FoodSearchAsyncTask();
                foodSearchAsyncTask.execute("cheddar");
            }
        });
        return vDailyDiet;
    }

    private List<String> getData() {

        list.add("hello");
        list.add("this is johnsonHou");
        list.add("an android rookie developer");
        list.add("love android");
        return list;
    }

    @Override
    public void onRefresh() {
//        new myTask().excute();
        new Handler().postDelayed(new Runnable() {
            public void run() {

                //显示或隐藏刷新进度条
                swipeRefreshLayout.setRefreshing(false);
                //修改adapter的数据
                list.add("这是新添加的数据");
                adapter.notifyDataSetChanged();
                //isRefresh = false;
            }
        }, 2000);
    }

    private class FoodSearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String...params) {
            return RestClient.searchFood(params[0]);
        }

        @Override
        protected void onPostExecute (String result){
            TextView resultTextView = vDailyDiet.findViewById(R.id.text_diet);
            Gson gson = new Gson();
            FoodSearchList foodSearchList = gson.fromJson(result, FoodSearchList.class);

            System.out.println(foodSearchList.list.total);
            resultTextView.setText(result);
        }

    }

}


