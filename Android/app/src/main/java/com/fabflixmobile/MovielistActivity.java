package com.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MovielistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        Intent originIntent = getIntent();
        Bundle originBundle = originIntent.getExtras();
        String q = originBundle.getString("q");
        Toast.makeText(getApplicationContext(), q, Toast.LENGTH_SHORT).show();
    }
}