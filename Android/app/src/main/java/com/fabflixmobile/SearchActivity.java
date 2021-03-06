package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchBox;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = (SearchView) findViewById(R.id.searchView);
        goButton = (Button) findViewById(R.id.buttonGo);


        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String searchQuery = searchBox.getQuery().toString();
                Log.d("SearchActivity", searchQuery);

                Intent intent = new Intent(SearchActivity.this, MovielistActivity.class);
                intent.putExtra("q", searchQuery);
                intent.putExtra("page", "1");

                startActivity(intent);
            }
        });
    }
}