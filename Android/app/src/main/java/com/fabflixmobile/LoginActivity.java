package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView message;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        message = findViewById(R.id.textViewMessage);

        // url for accessing login api
        url = "https://10.0.2.2:8443/Fabflix/api/login";

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
        // request type is POST
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                try {
                    JSONObject responseJsonObject = new JSONObject(response);
                    if (responseJsonObject.get("status").equals("success")) {
                        loginSuccessful();
                    }
                    else {
                        loginFailure(responseJsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("login.error", error.toString());
                    message.setText("Error");
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        queue.add(loginRequest);
    }

    private void loginSuccessful() {
        Log.d("login", "Success");
        message.setText("Success.");
    }

    private void loginFailure(JSONObject responseJsonObject) throws JSONException {
        Log.d("login", "Failure");
        message.setText((String) responseJsonObject.get("message"));
    }
}