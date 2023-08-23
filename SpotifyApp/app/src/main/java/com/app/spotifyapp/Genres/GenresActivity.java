package com.app.spotifyapp.Genres;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.app.spotifyapp.Database.FirebaseRepo;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.databinding.ActivityGenresBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class GenresActivity extends AppCompatActivity {

    private ActivityGenresBinding _binding;
    private final String[] genresName = {"Pop", "Rock", "Hip-Hop", "Edm", "Country", "Jazz", "Classical", "Rnb-Soul", "Indie alternative", "Latin"};

    String[] pop = {
            "Taylor Swift", "Ariana Grande", "Billie Eilish", "Justin Bieber", "Dua Lipa",
            "BTS", "Ed Sheeran", "Shawn Mendes", "Harry Styles", "Rihanna",
            "Katy Perry", "Bruno Mars", "Selena Gomez", "The Weeknd", "Maroon 5"
    };
    String[] rock = {
            "Foo Fighters", "Coldplay", "Imagine Dragons", "Twenty One Pilots", "Muse",
            "The Killers", "Arctic Monkeys", "Linkin Park", "Green Day", "Red Hot Chili Peppers",
            "Queen", "AC/DC", "Pearl Jam", "Fall Out Boy", "Panic! at the Disco"
    };

    String[] hip_hop = {
            "Kendrick Lamar", "Drake", "Travis Scott", "J Cole", "Kanye West",
            "ASAP Rocky", "Post Malone", "Lil Baby", "Pusha T", "Future",
            "Nas", "Migos", "Jay-Z", "Nicki Minaj", "Tyler, The Creator"
    };

    String[] edm = {
            "Martin Garrix", "The Chainsmokers", "Calvin Harris", "Marshmello", "Avicii",
            "David Guetta", "Zedd", "Skrillex", "Tiësto", "Daft Punk",
            "Diplo", "Major Lazer", "Afrojack", "Alesso", "Kygo"
    };

    String[] country = {
            "Luke Bryan", "Blake Shelton", "Jason Aldean", "Carrie Underwood", "Florida Georgia Line",
            "Thomas Rhett", "Miranda Lambert", "Keith Urban", "Kenny Chesney", "Tim McGraw",
            "Dierks Bentley", "Brad Paisley", "Eric Church", "Sam Hunt", "Maren Morris"
    };

    String[] jazz = {
            "Kamasi Washington", "Snarky Puppy", "Gregory Porter", "Esperanza Spalding", "Norah Jones",
            "Diana Krall", "Herbie Hancock", "Chick Corea", "Pat Metheny", "Brad Mehldau",
            "Wynton Marsalis", "Miles Davis", "John Coltrane", "Bill Evans", "Charlie Parker"
    };

    String[] classical = {
            "Lang Lang", "Yo-Yo Ma", "Itzhak Perlman", "Joshua Bell", "Anna Netrebko",
            "Renee Fleming", "Gustavo Dudamel", "Daniel Barenboim", "Plácido Domingo", "Yuja Wang",
            "Hilary Hahn", "András Schiff", "Yoav Talmi", "Anne-Sophie Mutter", "Vladimir Horowitz"
    };

    String[] rnb_soul = {
            "Beyoncé", "Frank Ocean", "Alicia Keys", "John Legend", "HER",
            "Anderson Paak", "Solange", "SZA", "Miguel", "Janelle Monáe",
            "The Weeknd", "Bruno Mars", "Lizzo", "Erykah Badu", "Usher"
    };

    String[] indie_alternative = {
            "Tame Impala", "Vampire Weekend", "Arctic Monkeys", "Radiohead", "Arcade Fire",
            "The Strokes", "Alt-J", "Florence + The Machine", "The National", "The Black Keys",
            "Foster the People", "Mumford & Sons", "The xx", "Bon Iver", "Lana Del Rey"
    };

    String[] latin = {
            "Bad Bunny", "J Balvin", "Karol G", "Maluma", "Romeo Santos",
            "Shakira", "Daddy Yankee", "Ozuna", "Marc Anthony", "Becky G",
            "Anitta", "Luis Fonsi", "Natti Natasha", "Juanes", "Sebastián Yatra"
    };
    private final String[][] genres = {pop, rock, hip_hop, edm, country, jazz, classical, rnb_soul, indie_alternative, latin};
    private FirebaseRepo db;
    private int count = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityGenresBinding.inflate(getLayoutInflater());
        View view = _binding.getRoot();
        setContentView(view);

        db = FirebaseRepo.getInstance();
        db.setRef();

        _binding.btnSkipGenres.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }


    @Override
    protected void onStart() {
        super.onStart();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genresName);
        _binding.lvGenres.setAdapter(adapter);
        _binding.lvGenres.setOnItemClickListener((adapterView, view, i, l) -> {

            --count;
            ChangeTVCount();

            db.SelectAll(genresName[i], artistsData -> {
                db.AddArtists(artistsData);
                if (count == 0) startActivity(new Intent(this, MainActivity.class));
            });

        });






//        for (int i = 0; i<genres.length; i++){
//            for (int j = 0; j<genres[i].length; j++) {
//                myRef.child(genresName[i]).child(genres[i][j]).get().addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    }
//                    else {
////                        Log.e("firebase", String.valueOf(task.getResult().getValue()));
//                        ArtistDAO ar = task.getResult().getValue(ArtistDAO.class);
////                        Log.e("onStart: ", Objects.requireNonNull(ar).Name);
//                    }
//
//                });
//            }
//
//        }


    }

    private void ChangeTVCount(){
        _binding.tvGenresCount.setText(String.format("Select max %d genres", count));
    }


}
