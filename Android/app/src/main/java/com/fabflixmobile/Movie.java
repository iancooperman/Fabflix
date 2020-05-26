package com.fabflixmobile;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private String year;
    private String director;
    private ArrayList<String> actors;
    private ArrayList<String> genres;

    public Movie(String id, String title, String year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.actors = new ArrayList<String>();
        this.genres = new ArrayList<String>();
    }

    public void addActor(String actor) {
        actors.add(actor);
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public String getID() {
        return new String(id);
    }

    public String getTitle() {
        return new String(title);
    }

    public String getYear() {
        return new String(year);
    }

    public String getDirector() {
        return new String(director);
    }

    public ArrayList<String> getActors() {
        return new ArrayList<String>(actors);
    }

    public ArrayList<String> getGenres() {
        return new ArrayList<String>(genres);
    }
}
