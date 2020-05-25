package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovielistActivity extends AppCompatActivity {
    private ListView listview;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // set up widgets
        listview = (ListView) findViewById(R.id.listview);
        Movie movie1 = new Movie("Arrival", "2016", "Who cares?");
        movie1.addActor("Marlon Brando");
        movie1.addActor("Some Guy");
        movie1.addGenre("Sci-Fi");
        movie1.addGenre("Horror");
        Movie movie2 = new Movie("Toy Story", "1990s", "Who cares?");
        movie2.addActor("Marlon Brando");
        movie2.addActor("Some Guy");
        movie2.addGenre("Sci-Fi");
        movie2.addGenre("Horror");

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        movieList.add(movie1);
        movieList.add(movie2);

        MovieListAdapter adapter = new MovieListAdapter(getApplicationContext(), R.layout.movie_layout, movieList);
        listview.setAdapter(adapter);


        Bundle originBundle = getIntent().getExtras();
        String q = originBundle.getString("q");

        url = Utility.url + "/api/movielist?q=" + q  + "&title=&year=0&director=&star=&genre=0&limit=20&page=1&sortBy=rating_desc_title_asc";

        retrieveMovieList();
    }

    private void retrieveMovieList() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movielistRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Movielist.success", "Success?");
                // TODO should parse the json response to redirect to appropriate functions.
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);

                    JSONArray movies = jsonObject.getJSONArray("movies");
                    if (movies.length() >= 1) {
                        String movieTitle = movies.getJSONObject(0).getString("movie_title");
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