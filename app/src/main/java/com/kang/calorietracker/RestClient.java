package com.kang.calorietracker;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import helper.User;

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/CalorieTrackerServer/webresources/";
    private static final String FOOD_API_URL = "http://api.nal.usda.gov/ndb/";
    private static final String FOOD_API_KEY = "nu277bbsmeOiL8nCIVQRn4hSuzcMWCJKOV4O6xgP";
    public static String register(User user) {
        final String methodPath = "restws.credential/register/";
        Gson gson = new Gson();
        final String json = gson.toJson(user);
        System.out.println(json);

        URL url = null;
        HttpURLConnection conn = null;
        String result = "";
        try {
            url = new URL(BASE_URL + methodPath);
            conn = (HttpURLConnection)url.openConnection();
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
        final String restOfUrl = "&format=json&sort=n&max=5";
        URL url;
        HttpURLConnection conn;
        String result = "";
        try {
            url = new URL(FOOD_API_URL + methodPath + FOOD_API_KEY + "&query=" + query + restOfUrl);
            conn = (HttpURLConnection)url.openConnection();
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
}
