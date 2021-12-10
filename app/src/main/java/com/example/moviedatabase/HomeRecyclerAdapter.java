package com.example.moviedatabase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allPostsRef = database.getReference("MovieData");
    ChildEventListener usersRefListener;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Marker currentMarker =null;
    // private RecyclerView r;
    //private  ItemClickListener itemClickListener;
    private List<String> keyList;
    private HashMap<String,MovieDataModel> key_to_Post;
    public HomeRecyclerAdapter(HashMap<String,MovieDataModel> kp, List<String> kl){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        keyList=kl;
        key_to_Post= kp;
//        itemClickListener =_itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card_view, parent,false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MovieDataModel u =key_to_Post.get(keyList.get(position));
        final String uid=u.uid;
        if(holder.uref!=null && holder.urefListener!=null)
        {
            holder.uref.removeEventListener(holder.urefListener);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        holder.uref = database.getReference("MovieData").child(uid);
        holder.uref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.movie_title.setText(dataSnapshot.child("title").getValue().toString());
                holder.movie_director.setText("Director: " + dataSnapshot.child("director").getValue().toString());
                holder.movie_stars.setText("Cast: " + dataSnapshot.child("stars").getValue().toString());
                holder.year.setText(dataSnapshot.child("year").getValue().toString());
                if (dataSnapshot.child("image").exists()) {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                            .transform(new CircleTransform()).into(holder.movie_image);
                    holder.movie_image.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeListener(){
        if(allPostsRef !=null && usersRefListener!=null)
            allPostsRef.removeEventListener(usersRefListener);
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView movie_title;
        public ImageView movie_image;
        public TextView movie_director;
        public TextView movie_stars;
        public TextView year;

        DatabaseReference uref;
        ValueEventListener urefListener;

        DatabaseReference likeCountRef;
        ValueEventListener likeCountRefListener;


        public ViewHolder(View v){
            super(v);
            movie_title = (TextView) v.findViewById(R.id.movie_title);
            movie_image = (ImageView) v.findViewById(R.id.movie_image);
            movie_director = (TextView) v.findViewById(R.id.director);
            movie_stars = (TextView) v.findViewById(R.id.stars);
            year = (TextView) v.findViewById(R.id.year);

        }
    }

}
