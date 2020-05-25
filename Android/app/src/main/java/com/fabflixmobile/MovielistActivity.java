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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MovielistActivity extends AppCompatActivity {
    private ListView listview;
    private ArrayList<Movie> movieList;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // set up widgets
        listview = (ListView) findViewById(R.id.listview);
        movieList = new ArrayList<Movie>();

        MovieListAdapter adapter = new MovieListAdapter(getApplicationContext(), R.layout.movie_layout, movieList);
        listview.setAdapter(adapter);


        Bundle originBundle = getIntent().getExtras();
        String q = null;
        try {
            q = URLEncoder.encode(originBundle.getString("q"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url = Utility.url + "/api/movielist?q=" + q  + "&title=&year=0&director=&star=&genre=0&limit=20&page=1&sortBy=rating_desc_title_asc";
        Log.d("MovielistActivity", url);

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
                    for (int i = 0; i < movies.length(); i++) {
                        // retrieve information for creating Movie object
                        JSONObject movieJSONObject = movies.getJSONObject(i);
                        String movieTitle = movieJSONObject.getString("movie_title");
                        String movieYear = movieJSONObject.getString("movie_year");
                        String movieDirector = movieJSONObject.getString("movie_director");
                        JSONArray movieActors = movieJSONObject.getJSONArray("movie_stars");
                        JSONArray movieGenres = movieJSONObject.getJSONArray("movie_genres");

                        Movie movie = new Movie(movieTitle, movieYear, movieDirector);
                        for (int j = 0; j < movieActors.length() ; j++) {
                            movie.addActor(movieActors.getJSONObject(j).getString("star_name"));
                        }
                        for (int j = 0; j < movieGenres.length() ; j++) {
                            movie.addGenre(movieGenres.getJSONObject(j).getString("genre_name"));
                        }

                        movieList.add(movie);
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