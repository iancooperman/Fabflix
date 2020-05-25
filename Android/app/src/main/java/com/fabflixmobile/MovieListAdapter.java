package com.fabflixmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    int mResource;

    public MovieListAdapter(@NonNull Context context, int resource, @NonNull List<Movie> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getTitle();
        String year = getItem(position).getYear();
        String director = getItem(position).getDirector();
        ArrayList<String> genres = getItem(position).getGenres();
        ArrayList<String> actors = getItem(position).getActors();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewTitleYear = (TextView) convertView.findViewById(R.id.textViewTitleYear);
        TextView textViewGenres = (TextView) convertView.findViewById(R.id.textViewGenres);
        TextView textViewDirector = (TextView) convertView.findViewById(R.id.textViewDirector);
        TextView textViewActors = (TextView) convertView.findViewById(R.id.textViewActors);

        textViewTitleYear.setText(title + " (" + year + ")");

        String genresString = String.join(", ", genres);
        textViewGenres.setText(genresString);

        textViewDirector.setText(director);

        String actorsString = String.join(", ", actors);
        textViewActors.setText(actorsString);

        return convertView;
    }
}
