package com.app.spotifyapp.Genres;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.app.spotifyapp.Database.FirebaseRepo;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.databinding.ActivityGenresBinding;

import java.util.Locale;

public class GenresActivity extends AppCompatActivity {

    private ActivityGenresBinding _binding;
    private final String[] genresName = {"Pop", "Rock", "Hip-Hop", "Edm", "Country", "Jazz", "Classical", "Rnb-Soul", "Indie alternative", "Latin"};
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
    }

    private void ChangeTVCount(){
        _binding.tvGenresCount.setText(String.format(Locale.getDefault(), "Select max %d genres", count));
    }


}
