package com.app.spotifyapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.app.spotifyapp.Adapters.ArtistsGridViewAdapter;
import com.app.spotifyapp.Database.FirebaseRepo;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.databinding.FragmentArtistsBinding;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    private FragmentArtistsBinding _binding;
    private final ArrayList<ArtistDAO>[] artistsDatas = new ArrayList[]{new ArrayList<>()};
    private ArtistsGridViewAdapter adapter;
    private FirebaseRepo db;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _binding = FragmentArtistsBinding.inflate(inflater, container, false);

        db = FirebaseRepo.getInstance();
        db.setRef();


        ArrayList<ArtistDAO> finalArtistsData = artistsDatas[0];

        adapter = new ArtistsGridViewAdapter(requireActivity(), finalArtistsData);
        adapter.setOnArtistClickListener(artist -> {
            Bundle bundle = new Bundle();
            bundle.putString("artistUri", artist.Id);
            bundle.putString("artistName", artist.Name);
            bundle.putString("artistImg", artist.Img);
            Navigation.findNavController(requireView()).navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });

        adapter.setOnArtistLongClickListener(artist -> {
            requireActivity().runOnUiThread(()->{
                Toast.makeText(requireContext(), "Deleting " + artist.Name, Toast.LENGTH_SHORT).show();
                db.Delete(artist);
                artistsDatas[0].remove(artist);
                adapter.UpdateData(artistsDatas[0]);
                adapter.notifyDataSetChanged();
            });

        });

        _binding.gvArtists.setAdapter(adapter);

        db.SelectYours(artistsData -> {
            artistsDatas[0].clear();
            artistsDatas[0].addAll(artistsData);
            adapter.UpdateData(artistsData);
            adapter.notifyDataSetChanged();
        });

        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        _binding.delete.setOnClickListener(v ->{
//            db.dele();
//            artistsDatas[0].clear();
//            adapter.UpdateData(artistsDatas[0]);
//            adapter.notifyDataSetChanged();
//        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}