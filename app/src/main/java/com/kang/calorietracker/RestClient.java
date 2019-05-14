package com.kang.calorietracker;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import helper.User;

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/CalorieTrackerServer/webresources/";
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

        } finally {
            //conn.disconnect();
            return result;
        }

    }
}
