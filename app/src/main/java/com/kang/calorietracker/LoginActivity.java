package com.kang.calorietracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Log In");
        setContentView(R.layout.activity_login);
        final EditText usernameEdit = findViewById(R.id.username_login);
        final TextInputLayout usernameWrapper = findViewById(R.id.wrapper_login_username);
        final EditText passwordEdit = findViewById(R.id.password_login);
        final TextInputLayout passwordWrapper = findViewById(R.id.wrapper_login_password);
        final Button loginButton = findViewById(R.id.login_button);
        final Button registerButton = findViewById(R.id.register_button);

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                if (username.equals("")) {
                    usernameWrapper.setError("Please enter your username!");
                }
                if (password.equals("")) {
                    passwordWrapper.setError("Please enter your password!");
                }
                else {
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        digest.update(password.getBytes());
                        String passwordhash = bytes2Hex(digest.digest());
                        System.out.println("Hash : " + passwordhash);
                        LogInAsyncTask logInAsyncTask = new LogInAsyncTask();
                        logInAsyncTask.execute(username, passwordhash);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Call login method from server, try to authenticate
     */
    private class LogInAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...params) {
            return RestClient.login(params[0], params[1]);
        }
        @Override
        protected void onPostExecute (String result) {
            if (result.equals("")) {
                Toast toast = Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                SharedPreferences userPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editer = userPreferences.edit();
                editer.putString("user", result);
                editer.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("loginStatus", true);
                startActivity(intent);
                Toast toast = Toast.makeText(LoginActivity.this, "Welcome, " + username, Toast.LENGTH_LONG);
                toast.show();
                finish();
                }
            }
    }

    // Convert byte[] to hex string
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
