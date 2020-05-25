package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovielistActivity extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        Bundle originBundle = getIntent().getExtras();
        String q = originBundle.getString("q");

        url = Utility.url + "/api/movielist?q=" + q;

        retrieveMovieList();
    }

    private void retrieveMovieList() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movielistRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Movielist.success", "Success?");
                // TODO should parse the json response to redirect to appropriate functions.
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String movieTitle = jsonObject.getString("movie_title");
                        Toast.makeText(getApplicationContext(), movieTitle, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Movielist.error", error.toString());
                }
            });

        queue.add(movielistRequest);
    }
}