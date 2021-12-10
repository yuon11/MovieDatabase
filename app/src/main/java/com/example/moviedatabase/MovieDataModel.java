package com.example.moviedatabase;

import android.util.Log;

public class MovieDataModel{

    public String uid;
    public String description;
    public String imageUrl;
    public String rating;
    public String stars;
    public String name;
    public String director;
    public String length;
    public String year;
    public String date;

    public MovieDataModel(String key, String name, String image, String description, String year,
                         String length, String rating, String director, String stars, String timestamp){
//        if(image=="" || image==null)
//            this.image="default_poster.jpg";
//        else
        Log.d("MovieData", "In Constructor "+ image + " "+ name + " "+ description + " ");
        this.uid=key;
        this.imageUrl=image;
        this.name = name;
        this.description=description;
        this.year=year;
        this.length=length;
        this.rating=rating;
        this.director=director;
        this.stars=stars;
        this.date=timestamp;
    }
}
