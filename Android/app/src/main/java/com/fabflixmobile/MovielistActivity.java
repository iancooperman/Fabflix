package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
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
    private Button prevButton;
    private Button nextButton;

    private ArrayList<Movie> movieList;
    private MovieListAdapter adapter;


    private String url;
    private String q;
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // set up widgets
        listview = (ListView) findViewById(R.id.listview);
        prevButton = (Button) findViewById(R.id.buttonPrev);
        nextButton = (Button) findViewById(R.id.buttonNext);

        movieList = new ArrayList<Movie>();

        adapter = new MovieListAdapter(getApplicationContext(), R.layout.movie_layout, movieList);
        listview.setAdapter(adapter);


        Bundle originBundle = getIntent().getExtras();
        q = originBundle.getString("q");
        page = originBundle.getString("page");

        Log.d("MovielistActivity", q);
        Log.d("MovielistActivity", page);


        // set up links to other pages
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MovielistActivity.class);
                intent.putExtra("q", q);
                intent.putExtra("page", Integer.toString(Integer.parseInt(page) - 1));

                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MovielistActivity.class);
                intent.putExtra("q", q);
                intent.putExtra("page", Integer.toString(Integer.parseInt(page) + 1));

                startActivity(intent);
            }
        });


        // remove the prevButton if this is the first page
        if (Integer.parseInt(page) == 1) {
            ((ViewManager)prevButton.getParent()).removeView(prevButton);
        }

        String qEncoded = null;
        try {
            qEncoded = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = Utility.url + "/api/movielist?q=" + qEncoded  + "&page=" + page + "&title=&year=0&director=&star=&genre=0&limit=20&sortBy=rating_desc_title_asc";
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
                    Log.d("MovielistActivity", response);
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

                        adapter.notifyDataSetChanged();
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