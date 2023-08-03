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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.Database.FirebaseRepo;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.databinding.FragmentArtistsAlbumsBinding;

import java.util.ArrayList;


public class ArtistsAlbumsFragment extends Fragment {

    private String artistID;
    private final ArrayList<AlbumDAO>[] albumData = new ArrayList[]{new ArrayList<>()};
    private String Scope = "album";
    private FragmentArtistsAlbumsBinding _binding;

    @Override
    public void onStart() {
        super.onStart();

        ChangeText();
        connected();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentArtistsAlbumsBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseRepo db = FirebaseRepo.getInstance();
        db.setRef();

        Bundle bundle = getArguments();
        if (bundle != null) {
            artistID = bundle.getString("artistUri");
            _binding.tv.setText(bundle.getString("artistName"));
        }

        _binding.tv.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_SecondFragment_to_FirstFragment));

        _binding.btnArtistSettings.setOnClickListener(v ->{
            String artistName = bundle.getString("artistName");
            Toast.makeText(requireContext(), "Adding " + artistName, Toast.LENGTH_SHORT).show();
            db.AddArtist(new ArtistDAO(artistName, bundle.getString("artistImg"), bundle.getString("artistUri")));
        });
    }


    private void connected(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.albumRecycler.setLayoutManager(linearLayoutManager);

        ArrayList<AlbumDAO> finalAlbumData = albumData[0];
        final AlbumsRecyclerViewAdapter adapter = new AlbumsRecyclerViewAdapter(getActivity(), finalAlbumData, album -> {
            Bundle bundle = new Bundle();
            bundle.putString("selectedAlbum", album.Name);
            bundle.putString("albumHref", album.Id);
            bundle.putString("albumImg", album.Img);

            Navigation.findNavController(requireView()).navigate(R.id.action_SecondFragment_to_albumsTrackList, bundle);
        });
        _binding.albumRecycler.setAdapter(adapter);

        _binding.toSingles.setOnClickListener((view -> {
            ChangeScopeHandler();
            getArtistsAlbums(adapter);
        }));

        getArtistsAlbums(adapter);
    }

    private void ChangeText(){
        if (Scope.equals("single")){
            _binding.selectedType.setText("Singles");
            _binding.toSingles.setText("Get Albums");
            return;
        }
        _binding.selectedType.setText("Albums");
        _binding.toSingles.setText("Get Singles");
    }

    private void ChangeScopeHandler(){
        if (Scope.equals("album")){
            Scope = "single";
        }else {
            Scope = "album";
        }
        ChangeText();
    }

    public void getArtistsAlbums(AlbumsRecyclerViewAdapter adapter){
        ApiDataProvider api = new ApiDataProvider();
        api.getArtistsAlbums(artistID, Scope, albumsData -> requireActivity().runOnUiThread(() -> {
            albumData[0].clear();
            albumData[0].addAll(albumsData);
            adapter.UpdateData(albumData[0]);
            adapter.notifyDataSetChanged();
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}