package com.app.spotifyapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.SearchRecyclerViewAdapter;
import com.app.spotifyapp.Models.SearchDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentSearchBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding _binding;
    private SpotifyAppRemote _SpotifyAppRemote;
    private final ArrayList<SearchDAO> searchData = new ArrayList<>();
    private final String[] searchTypes = new String[]{null, null, null};
    private final boolean[] ButtonsState = new boolean[]{false, false, false};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentSearchBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ApiDataProvider api = new ApiDataProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        _binding.rvSearch.setLayoutManager(linearLayoutManager);

        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(requireContext(), searchData, this::ManageOnSearchClick);
        _binding.rvSearch.setAdapter(adapter);

        _binding.btnSearchArtists.setOnClickListener(view -> {
            ManageOnTextViewClick("artist");
        });

        _binding.btnSearchAlbums.setOnClickListener(view -> {
            ManageOnTextViewClick("album");
        });
        _binding.btnSearchTracks.setOnClickListener(view -> {
            ManageOnTextViewClick("track");
        });

        _binding.btnSearch.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(view.getApplicationWindowToken(),0);

            if (isAnythingClicked()){
                api.Search(_binding.etSearch.getText().toString(), searchTypes, requireActivity(), data -> {
                    searchData.clear();
                    searchData.addAll(data);
                    adapter.UpdateData(searchData);
                    adapter.notifyDataSetChanged();
                });
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        _binding.etSearch.setText("");

        for (String s : searchTypes) {
            if (s!=null) Log.e("onStart: ", s);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        BackgroundOnRestart();
    }

//    private void setSearchType(String type){
//        if (searchType != null && searchType.equals(type)){
//            searchType = null;
//            return;
//        }
//        searchType = type;
//    }

    // TODO: 8/1/2023 Temporary function to check if any type of search is checked 
    private boolean isAnythingClicked(){
        for (boolean b : ButtonsState) {
            if (b) return true;
        }
        return false;
    }

    private void ManageOnTextViewClick(String type){
        requireActivity().runOnUiThread(() ->{
            if (type.equals("artist")){
                boolean isClicked = IsClicked(0);
                SetButtonBackground(isClicked, Objects.requireNonNull(GetButtonFromType(type)));
                SetSearchTypes(type, 0);
                return;
            }
            if (type.equals("album")) {
                boolean isClicked = IsClicked(1);
                SetButtonBackground(isClicked, Objects.requireNonNull(GetButtonFromType(type)));
                SetSearchTypes(type, 1);
                return;
            }
            boolean isClicked = IsClicked(2);
            SetButtonBackground(isClicked, Objects.requireNonNull(GetButtonFromType(type)));
            SetSearchTypes(type, 2);
        });
    }
    private void BackgroundOnRestart(){
        for (int i = 0; i < ButtonsState.length; i++) {
            boolean b = ButtonsState[i];
            if (b) {
                SetButtonBackground(true, Objects.requireNonNull(GetButtonFromType(searchTypes[i])));
            }
        }
    }
    private void SetSearchTypes(String type, int num){
        searchTypes[num] = searchTypes[num] == null ? type : null;
    }
    private boolean IsClicked(int num){
        ButtonsState[num] = !ButtonsState[num];
        return ButtonsState[num];
    }
    private void SetButtonBackground(boolean turnedOn, @NonNull TextView button){
        button.setBackgroundResource(turnedOn ? R.drawable.clicked_button : R.drawable.custom_button);
    }
    private void ManageOnSearchClick(SearchDAO search){
        Bundle bundle = new Bundle();
        String type = search.Type;
        if (type.equals("track")){
            if (_SpotifyAppRemote != null) {
                _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + search.Id);
                Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                startActivity(intent);
            }
        } else if (type.equals("album")) {
            bundle.putString("selectedAlbum", search.Name);
            bundle.putString("albumHref", search.Id);
            bundle.putString("albumImg", search.Img);
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_albumsTrackList, bundle);
        }else{
            bundle.putString("artistUri", search.Id);
            bundle.putString("artistName", search.Name);
            bundle.putString("artistImg", search.Img);
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_ArtistsAlbumsFragment, bundle);
        }

        Toast.makeText(requireContext(), search.Name, Toast.LENGTH_LONG).show();
    }
    private TextView GetButtonFromType(String type){
        switch(type){
            case "artist":
                return _binding.btnSearchArtists;
            case "album":
                return _binding.btnSearchAlbums;
            case "track":
                return _binding.btnSearchTracks;
        }
        return null;
    }

}