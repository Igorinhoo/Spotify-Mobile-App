package com.app.spotifyapp.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.ArtistDataCallback;
import com.app.spotifyapp.Models.ArtistDAO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseRepo {

    // TODO: 8/23/2023 Change rules in database to be to different identity 
    private static FirebaseRepo instance;
    private DatabaseReference myRef;
    private static FirebaseDatabase database;

    public static FirebaseRepo getInstance(){
        database = FirebaseDatabase.getInstance();
        if (instance == null){
            instance = new FirebaseRepo();
        }
        return instance;

    }

    private FirebaseRepo(){}

    public void setRef(){
        myRef = database.getReference();
    }

    public void SelectAll(String object, ArtistDataCallback callback){
        ArrayList<ArtistDAO> artists = new ArrayList<>();
        myRef.child("Genres").child(object).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()
                ) {
                    ArtistDAO artist = data.getValue(ArtistDAO.class);
                    artists.add(artist);
                    Log.e("TAG", "Data: " + Objects.requireNonNull(artist).Name);
                }
                callback.onArtistsDataReceived(artists);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
            }
        });
    }
    public void SelectYours(ArtistDataCallback callback){
        ArrayList<ArtistDAO> artists = new ArrayList<>();
        myRef.child("YourArtists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()
                ) {
                    ArtistDAO artist = data.getValue(ArtistDAO.class);
                    artists.add(artist);
                }
                callback.onArtistsDataReceived(artists);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
            }
        });
    }

    public void AddArtist(ArtistDAO artist){
        myRef.child("YourArtists").child(artist.Id).setValue(artist);
    }

    public void AddArtists(ArrayList<ArtistDAO> artists){
        for (ArtistDAO artist: artists
             ) {
            myRef.child("YourArtists").child(artist.Id).setValue(artist);
        }
    }

    public void Delete(ArtistDAO artist){
        myRef.child("YourArtists").child(artist.Id).removeValue();
    }
    public void DeleteMany(ArrayList<ArtistDAO> artists){
        for (ArtistDAO artist:artists
             ) {
            myRef.child("YourArtists").child(artist.Id).removeValue();
        }
    }
    public void DeleteAllUserArtists(){
        myRef.child("YourArtists").removeValue();
    }
}
