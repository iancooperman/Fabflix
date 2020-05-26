package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    private TextView titleYearView;
    private TextView genresView;
    private TextView directorView;
    private TextView actorsView;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        titleYear = (TextView) findViewById(R.id.textViewTitleYearSingleMovie);
        genres = (TextView) findViewById(R.id.textViewGenresSingleMovie);
        director = (TextView) findViewById(R.id.textViewDirectorSingleMovie);
        actors = (TextView) findViewById(R.id.textViewActorsSingleMovie);

        Bundle originBundle = getIntent().getExtras();
        String id = originBundle.getString("id");

        url = Utility.url + "/api/movie?=id" + id;

        retrieveMovieInfo();

    }

    private void retrieveMovieInfo() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SingleMovie.success", "Success?");

                // parse the incoming JSONObject
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String title = jsonObject.getString("movie_title");
                    String year = jsonObject.getString("movie_year");
                    String director = jsonObject.getString("movie_director");
                    JSONArray genres = jsonObject.getJSONArray("movie_genres");
                    JSONArray actors = jsonObject.getJSONArray("movie_stars");

                    ArrayList<String> genreStrings = new ArrayList<>();
                    ArrayList<String> actorStrings = new ArrayList<>();

                    for (int i = 0; i < genres.length(); i++) {
                        genreStrings.add(genres.getJSONObject(i).getString("genre_name"));
                    }

                    String genreText = String.join(", ", genreStrings);

                    for (int i = 0; i < actors.length(); i++) {
                        actorStrings.add(genres.getJSONObject(i).getString("actor_name"));
                    }

                    String actorText = String.join(", ", actorStrings);

                    titleYearView.setText(title + " (" + year + ")");
                    directorView.setText("Director: " + director);
                    genresView.setText(genreText);
                    actorsView.setText(actorText);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.d("SingleMovie.error", error.toString());
           }
        });

        queue.add(request);
    }
}