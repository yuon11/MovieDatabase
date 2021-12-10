package com.example.moviedatabase;

import android.util.Log;

import com.google.firebase.database.ServerValue;

public class MovieData {

    public String description;
    public String length;
    public String year;
    public double rating;
    public String director;
    public String stars;
    public String title;
    public String image;
    public Object timestamp;

    public MovieData(String name, String image, String description, String year,
                     String length, double rating, String director, String stars){
//        if(image=="" || image==null)
//            this.image="default_poster.jpg";
//        else
        Log.d("MovieData", "In Constructor "+ image + " "+ name + " "+ description + " ");
        this.image=image;
        this.title = name;
        this.description=description;
        this.year=year;
        this.length=length;
        this.rating=rating;
        this.director=director;
        this.stars=stars;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public MovieData() {

    }
}
