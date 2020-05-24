package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView message;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        message = findViewById(R.id.textViewMessage);

        // url for accessing login api
        url = "https://10.0.0.2:8443/Fabflix/api/login";

        // set on click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        message.setText("Attempting to login. Please wait.");

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

    }
}