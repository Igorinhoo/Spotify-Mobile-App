package com.app.spotifyapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TopArtistsRecyclerViewAdapter;
import com.app.spotifyapp.Adapters.TopTracksRecyclerViewAdapter;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.StatisticsTerm;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.StatisticsAPIDataProvider;
import com.app.spotifyapp.databinding.FragmentStatisticsBinding;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment{

    private final ArrayList<ArtistDAO> ArtistData = new ArrayList<>();
    private final ArrayList<TrackDAO> TrackData = new ArrayList<>();
    private FragmentStatisticsBinding _binding;
    private static final LinearLayout.LayoutParams weight1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
    private static final LinearLayout.LayoutParams weight0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0, 0);
    private TopArtistsRecyclerViewAdapter adapterArtists;
    private TopTracksRecyclerViewAdapter adapterTracks;
    private StatisticsAPIDataProvider api;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new StatisticsAPIDataProvider();
        weight0.setMargins(8,8,8,8);
        weight1.setMargins(8,8,8,8);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false);

        _binding.topTracksRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        _binding.topAlbumsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapterArtists = new TopArtistsRecyclerViewAdapter(ArtistData);
        _binding.topAlbumsRecycler.setAdapter(adapterArtists);

        adapterTracks = new TopTracksRecyclerViewAdapter(requireActivity(), TrackData);
        _binding.topTracksRecycler.setAdapter(adapterTracks);

        GetStatistics();

        return _binding.getRoot();
    }

    private void GetStatistics(int value){
        StatisticsTerm term = StatisticsTerm.LONG_TERM;
        switch (value){
            case 0:
                term = StatisticsTerm.SHORT_TERM;
                break;
            case 1:
                term = StatisticsTerm.MEDIUM_TERM;
                break;
        }

        api.getTopTracks(MainActivity.getAccessToken(), term, trackData -> requireActivity().runOnUiThread(() -> {
            TrackData.clear();
            TrackData.addAll(trackData);
            adapterTracks.UpdateData(TrackData);
            adapterTracks.notifyDataSetChanged();
            _binding.topAlbumsRecycler.scrollToPosition(0);
        }));
        api.getTopArtists(MainActivity.getAccessToken(), term, artistsData -> requireActivity().runOnUiThread(() -> {
            ArtistData.clear();
            ArtistData.addAll(artistsData);
            adapterArtists.UpdateData(ArtistData);
            adapterArtists.notifyDataSetChanged();
            _binding.topTracksRecycler.scrollToPosition(0);
        }));
    }

    private void GetStatistics(){
        int value = requireActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getInt("statsTerm", 2);
        StatisticsTerm term = StatisticsTerm.LONG_TERM;
        switch (value){
            case 0:
                term = StatisticsTerm.SHORT_TERM;
                break;
            case 1:
                term = StatisticsTerm.MEDIUM_TERM;
                break;
        }

        api.getTopTracks(MainActivity.getAccessToken(), term, trackData -> requireActivity().runOnUiThread(() -> {
            TrackData.clear();
            TrackData.addAll(trackData);
            adapterTracks.UpdateData(TrackData);
            adapterTracks.notifyDataSetChanged();
            _binding.topAlbumsRecycler.scrollToPosition(0);
        }));
        api.getTopArtists(MainActivity.getAccessToken(), term, artistsData -> requireActivity().runOnUiThread(() -> {
            ArtistData.clear();
            ArtistData.addAll(artistsData);
            adapterArtists.UpdateData(ArtistData);
            adapterArtists.notifyDataSetChanged();
            _binding.topTracksRecycler.scrollToPosition(0);
        }));
    }

    private void ShowTermDialog(){
        Dialog dialog = new Dialog(requireContext(), R.style.CustomButtonsDialog);
        dialog.setContentView(R.layout.statistics_choice);

        Button short_term = dialog.findViewById(R.id.btn_short_term);
        Button medium_term = dialog.findViewById(R.id.btn_medium_term);
        Button long_term = dialog.findViewById(R.id.btn_long_term);

        Button[] termButtons = {short_term, medium_term, long_term};
        for (int i = 0; i < 3; i++) {
            final int finalI = i;

            int value = requireActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getInt("statsTerm", 2);
            if (i == value) termButtons[i].setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.basic));
            else termButtons[i].setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttons));

            termButtons[i].setOnClickListener(view -> {
                requireActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit().putInt("statsTerm", finalI).apply();
                GetStatistics(finalI);
                dialog.dismiss();
            });
        }
        dialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();

        _binding.btnStatisticsSettings.setOnClickListener(view -> ShowTermDialog());

        _binding.tracks.setOnClickListener((view -> {
            _binding.topAlbumsRecycler.setLayoutParams(weight0);
            _binding.topTracksRecycler.setLayoutParams(weight1);

        }));


        _binding.artists.setOnClickListener(view -> {
            _binding.topAlbumsRecycler.setLayoutParams(weight1);
            _binding.topTracksRecycler.setLayoutParams(weight0);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}