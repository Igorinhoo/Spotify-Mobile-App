package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.Database.FirebaseRepo;
import com.app.spotifyapp.Games.GamesActivity;
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
            artistID = bundle.getString("artistID");
            _binding.tv.setText(bundle.getString("artistName"));
        }

        _binding.tv.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_SecondFragment_to_FirstFragment));

        _binding.btnArtistSettings.setOnClickListener(v -> ArtistPopup(bundle, db));
    }

    private void ArtistPopup(Bundle bundle, FirebaseRepo db){
        PopupMenu popupMenu = new PopupMenu(requireContext(), _binding.btnArtistSettings);
        popupMenu.getMenuInflater().inflate(R.menu.artist_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.popupAddArtist:
                    String artistName = bundle.getString("artistName");
                    Toast.makeText(requireContext(), "Adding " + artistName, Toast.LENGTH_SHORT).show();
                    db.AddArtist(new ArtistDAO(artistName, bundle.getString("artistImg"), bundle.getString("artistID")));
                    break;
                case R.id.popupArtistGame:
                    Intent intent = new Intent(requireContext(), GamesActivity.class);
                    intent.putExtra("ID", bundle.getString("artistID"));
                    startActivity(intent);
                    return true;
            }
            return false;
        });
        popupMenu.show();

    }
    @Override
    public void onStart() {
        super.onStart();

        ChangeText();
        connected();


    }



    private void connected(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.albumRecycler.setLayoutManager(linearLayoutManager);

        ArrayList<AlbumDAO> finalAlbumData = albumData[0];
        final AlbumsRecyclerViewAdapter adapter = new AlbumsRecyclerViewAdapter(finalAlbumData, album -> {
            Bundle bundle = new Bundle();
            bundle.putString("selectedAlbum", album.getName());
            bundle.putString("albumHref", album.getId());
            bundle.putString("albumImg", album.getImages()[0].getUrl());
            bundle.putString("albumReleaseDate", album.getReleaseDate());

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
            _binding.selectedType.setText(R.string.singles);
            _binding.toSingles.setText(R.string.get_albums);
            return;
        }
        _binding.selectedType.setText(R.string.albums);
        _binding.toSingles.setText(R.string.get_singles);
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