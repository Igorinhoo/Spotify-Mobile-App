package com.app.spotifyapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Callbacks.AlbumDataCallback;
import com.app.spotifyapp.Interfaces.Listeners.OnAlbumClickListener;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentArtistsAlbumsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;

import java.util.ArrayList;


public class ArtistsAlbumsFragment extends Fragment {

    private String data;
    private final ErrorCallback mErrorCallback = this::logError;
    private final ArrayList<AlbumDAO>[] albumData = new ArrayList[]{new ArrayList<>()};

    private String Scope = "album";
    private SpotifyAppRemote mSpotifyAppRemote;

    private void logError(Throwable error) {
        System.err.println("Error occurred: " + error.getMessage());
    }
    private FragmentArtistsAlbumsBinding _binding;

    @Override
    public void onStart() {
        super.onStart();

        try {
            SpotifyAppRemoteConnector.Connect(getActivity());
            connected();
        }catch (Exception e){
            Log.d("Second Fragment ", "Nie polaczone");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentArtistsAlbumsBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getString("artistUri");
            _binding.tv.setText(bundle.getString("artistName"));
        }

        _binding.tv.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_SecondFragment_to_FirstFragment));
    }


    private void connected(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.albumRecycler.setLayoutManager(linearLayoutManager);

        ArrayList<AlbumDAO> finalAlbumData = albumData[0];
        AlbumsRecyclerViewAdapter adapter = new AlbumsRecyclerViewAdapter(getActivity(), finalAlbumData, new OnAlbumClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("selectedAlbum", finalAlbumData.get(position).Name);
                bundle.putString("albumHref", finalAlbumData.get(position).Uri);
                bundle.putString("albumImg", finalAlbumData.get(position).Img);

                Navigation.findNavController(getView()).navigate(R.id.action_SecondFragment_to_albumsTrackList, bundle);
            }
        });
        _binding.albumRecycler.setAdapter(adapter);

        _binding.toSingles.setOnClickListener((view -> {
            if(_binding.toSingles.getText() == "Get Singles"){
                _binding.selectedType.setText("Singles");

                Scope = "single";
                _binding.toSingles.setText("Get Albums");
            }
            else{
                _binding.selectedType.setText("Albums");
                Scope = "album";
                _binding.toSingles.setText("Get Singles");
            }
            getArtistsAlbums(adapter);
        }));
        getArtistsAlbums(adapter);
    }


    public void getArtistsAlbums(AlbumsRecyclerViewAdapter adapter){
        ApiDataProvider api = new ApiDataProvider();
        api.getArtistsAlbums(data, Scope, new AlbumDataCallback() {
            @Override
            public void onAlbumDataReceived(ArrayList<AlbumDAO> albumDatas) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        albumData[0].clear();
                        albumData[0].addAll(albumDatas);
                        adapter.UpdateData(albumData[0]);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}