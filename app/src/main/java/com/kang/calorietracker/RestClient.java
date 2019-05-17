package com.kang.calorietracker;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import helper.Food;
import helper.User;

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/CalorieTrackerServer/webresources/";
    private static final String FOOD_API_URL = "https://api.edamam.com/api/food-database/parser?";
    private static final String FOOD_APP_ID = "38f08e44";
    private static final String FOOD_APP_KEY = "73b190616559c2a37251bd769bfa3ce7";
    private static final String SEARCH_API_URL = "https://www.googleapis.com/customsearch/v1?cx=";
    private static final String SEARCH_API_CX = "013948155761704756549:rxkjvvneyxo";
    private static final String SEARCH_API_KEY = "AIzaSyC--qMr8pfwFuz_OJ11ZQij0dslIQg8kyw";

    public static String register(User user) {
        final String methodPath = "restws.credential/register/";
        Gson gson = new Gson();
        final String json = gson.toJson(user);
        System.out.println(json);

        URL url;
        HttpURLConnection conn;
        String result = "";
        try {
            url = new URL(BASE_URL + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(json.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream out = conn.getOutputStream();
            out.write(json.getBytes());
            out.flush();
            out.close();
            System.out.println("In POSTed");
            if (conn.getResponseCode() == 200) {
                //InputStream in = conn.getInputStream();
                Scanner inStream = new Scanner(conn.getInputStream());

                while (inStream.hasNextLine()) {
                    result += inStream.nextLine();
                }
                System.out.println("IN get result");
                System.out.print("Result: " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String searchFood(String query) {
        final String methodPath = "search/?api_key=";
        final String restOfUrl = "&format=json&sort=r&max=20";
        System.out.println(query);
        URL url;
        HttpURLConnection conn;
        String result = "";
        query = query.trim().replaceAll("\\s", "%20");
        try {
            url = new URL(FOOD_API_URL + "ingr=" + query + "&app_key=" + FOOD_APP_KEY + "&app_id=" + FOOD_APP_ID);
            System.out.println(url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                result += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String addFood(Food food) {
        final String methodPath = "restws.food";
        Gson gson = new Gson();
        final String json = gson.toJson(food);
        URL url;
        HttpURLConnection conn;
        String result = "";

        try {
            url = new URL(BASE_URL + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(json.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream out = conn.getOutputStream();
            out.write(json.getBytes());
            out.flush();
            out.close();
            if (conn.getResponseCode() == 204) {
                result = "Food added!";
            } else {
                result = "Failed to add new food!";
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static String googleSearch(String query) {
        URL url;
        HttpURLConnection conn;
        String result = "";
        query = query.trim().replaceAll("\\s", "%20");
        try {
            url = new URL(SEARCH_API_URL + SEARCH_API_CX + "&key=" + SEARCH_API_KEY + "&q=" + query + "&num=1");
           // url = new URL("http://baidu.com");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                result += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
