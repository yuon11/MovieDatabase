package com.example.moviedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MovieDataPreview extends AppCompatActivity {

    Uri uri;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data_preview);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void changePhoto(View view) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_FOR_LOCATION && ((grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) || (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED))) {
//            Toast.makeText(this, "We need to access your location", Toast.LENGTH_SHORT).show();
//        }
    }

    private void uploadMovieData() {
        try {
            final ImageView imageView;
            final EditText movie_title_view;
            final EditText year_view;
            final EditText length_view;
            final EditText director_view;
            final EditText stars_view;
            final EditText rating_view;
            final EditText description_view;

            imageView= findViewById(R.id.previewImage);
            movie_title_view = findViewById(R.id.title);
            length_view = findViewById(R.id.length);
            year_view = findViewById(R.id.year);
            director_view = findViewById(R.id.director);
            stars_view = findViewById(R.id.stars);
            rating_view = findViewById(R.id.rating);
            description_view = findViewById(R.id.description);

            // User took a pic or selected an image to upload
            //This part is added to eliminate the firebase cloud function
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference moviesRef = database.getReference("/MovieData");
            final DatabaseReference newPostRef = moviesRef.push();


//            final String rating_val = rating_view.getText().toString();
//            final String title =movie_title_view.getText().toString();
//            final String description =description_view.getText().toString();
//            final String year =year_view.getText().toString();
//            final String length =length_view.getText().toString();
//            final Double rating =Double.parseDouble(rating_val);
//            final String director =director_view.getText().toString();
//            final String stars =stars_view.getText().toString();

            Log.d("onSuccess"," "+movie_title_view.getText().toString());

            if (imageView!=null && uri!=null)
            {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final String fileNameInStorage = UUID.randomUUID().toString();
                String path = "images/" + fileNameInStorage +"_"+ movie_title_view.getText().toString() + ".jpg";
                final StorageReference imageRef = storage.getReference(path);
                final StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .setCustomMetadata("uid", currentUser.getUid())
                        .setCustomMetadata("description", description_view.getText().toString())
                        .build();
                //
                // Wait for new image upload before uploading movie data
                imageRef.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        newPostRef.setValue(new MovieData(movie_title_view.getText().toString(),
                                fileNameInStorage +"_"+ movie_title_view.getText().toString() + ".jpg",
                                description_view.getText().toString(),
                                year_view.getText().toString(),
                                length_view.getText().toString(),
                                Double.parseDouble(rating_view.getText().toString()),
                                director_view.getText().toString(),
                                stars_view.getText().toString()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override public void onSuccess(Void aVoid) {
                                        Toast.makeText(MovieDataPreview.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //----------------------------------------------
                        Toast.makeText(MovieDataPreview.this, "Upload completed. Your movie will appear shortly.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MovieDataPreview.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                newPostRef.setValue(new MovieData(movie_title_view.getText().toString(),
                        "default_poster.jpg",
                        description_view.getText().toString(),
                        year_view.getText().toString(),
                        length_view.getText().toString(),
                        Double.parseDouble(rating_view.getText().toString()),
                        director_view.getText().toString(),
                        stars_view.getText().toString()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override public void onSuccess(Void aVoid) {
                                Toast.makeText(MovieDataPreview.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                //----------------------------------------------
                Toast.makeText(MovieDataPreview.this, "Upload completed. Your movie will appear shortly.", Toast.LENGTH_SHORT).show();

            }

        } catch (NumberFormatException e) {
            // p did not contain a valid double
            Toast.makeText(getApplicationContext(),
                    "Rating did not contain a valid double - "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Publish(View view){
        uploadMovieData();
        finish();
    }
}