package com.example.moviedatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final int REQUEST_FOR_CAMERA = 0011;
    private static final int REQUEST_FOR_LOCATION = 0012;
    private Uri imageUri = null;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private RecyclerView recyclerView;
    private List<String> keyList = null;
    private HashMap<String, MovieDataModel> key_to_Post = null;
    SimpleDateFormat localDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allMovieDataRef = database.getReference("Posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        keyList = new ArrayList<>();
        key_to_Post = new HashMap<>();

        recyclerView=findViewById(R.id.recylcer_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        homeRecyclerAdapter=new HomeRecyclerAdapter(key_to_Post,keyList);
        recyclerView.setAdapter(homeRecyclerAdapter);

        Log.d("OnCreate","Starting Listener");

        allMovieDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                MovieDataModel userModel=new MovieDataModel(
                        dataSnapshot.getKey(),
                        dataSnapshot.child("title").getValue().toString(),
                        dataSnapshot.child("image").getValue().toString(),
                        dataSnapshot.child("description").getValue().toString(),
                        dataSnapshot.child("year").getValue().toString(),
                        dataSnapshot.child("length").getValue().toString(),
                        dataSnapshot.child("rating").getValue().toString(),
                        dataSnapshot.child("director").getValue().toString(),
                        dataSnapshot.child("stars").getValue().toString(),
                        localDateFormat.format(new Date(Long.parseLong(dataSnapshot.child("timestamp").getValue().toString())))
                );

                key_to_Post.put(dataSnapshot.getKey(),userModel);
                keyList.add(dataSnapshot.getKey());
                homeRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < keyList.size(); i++) {
                    if(keyList.get(i).equals(snapshot.getKey()))
                    {
                        MovieDataModel userModel=new MovieDataModel(
                                snapshot.getKey(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("image").getValue().toString(),
                                snapshot.child("description").getValue().toString(),
                                snapshot.child("year").getValue().toString(),
                                snapshot.child("length").getValue().toString(),
                                snapshot.child("rating").getValue().toString(),
                                snapshot.child("director").getValue().toString(),
                                snapshot.child("stars").getValue().toString(),
                                localDateFormat.format(new Date(Long.parseLong(snapshot.child("timestamp").getValue().toString())))
                        );

                        key_to_Post.put(keyList.get(i),userModel);
                        keyList.set(i, keyList.get(i));
                        homeRecyclerAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < keyList.size(); i++) {
                    if(keyList.get(i).equals(snapshot.getKey()))
                    {
                        key_to_Post.remove(snapshot.getKey());
                        keyList.remove(i);
                        homeRecyclerAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewMovie(View view) {
        Intent intent=new Intent(this, MovieDataPreview.class);
        // intent.putExtra("uri",imageUri.toString());
        startActivity(intent);
    }

    private void checkCamPermissions(){

        if (ContextCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "We need permission to access your camera and photo.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_FOR_CAMERA);
        }
        else
        {
            Toast.makeText(this, "Permission granted to access your camera and photo.", Toast.LENGTH_SHORT).show();
            takePhoto();
        }
    }

    private void takePhoto(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Movie Poster");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent chooser=Intent.createChooser(intent,"Select a Camera App.");
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d("Take Photo", "Trying to access camera");
            startActivityForResult(chooser, REQUEST_FOR_CAMERA);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_CAMERA && resultCode == RESULT_OK) {
            if(imageUri==null)
            {
                Toast.makeText(this, "Error taking photo.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(this, MovieDataPreview.class);
            intent.putExtra("uri",imageUri.toString());
            startActivity(intent);

            return;
        }
    }
}