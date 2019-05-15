package com.kang.calorietracker;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String test = null;
        test += "test";
        System.out.println(test);

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
                String username = usernameEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                if (username.equals("")) {
                    usernameWrapper.setError("Please enter your username!");
                }
                if (password.equals("")) {
                    passwordWrapper.setError("Please enter your password!");
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginStatus", true);
                    startActivity(intent);
                    Toast toast = Toast.makeText(LoginActivity.this, "Welcome, " + username, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
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
}
