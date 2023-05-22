package com.app.spotifyapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.app.spotifyapp.Adapters.ArtistsGridViewAdapter;
import com.app.spotifyapp.Interfaces.Callbacks.ArtistDataCallback;
import com.app.spotifyapp.Interfaces.OnArtistClickListener;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.databinding.FragmentArtistsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArtistsFragment extends Fragment {

    private FragmentArtistsBinding _binding;
    private GridView coursesGV;
    private final ArrayList<ArtistDAO>[] artistsDatas = new ArrayList[]{new ArrayList<>()};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _binding = FragmentArtistsBinding.inflate(inflater, container, false);


        List<String> artists = new ArrayList<>(
                Arrays.asList("5K4W6rqBFWDnAN6FQUkS6x", "2YZyLoL8N0Wb9xBt1NhZWg",
                        "3nFkdlSjzX9mRTtwJOzDYB", "0Y5tJX1MQlPlqiwlOH1tJY",
                        "20qISvAhX20dpIbOOzGK3q", "3TVXtAsR1Inumwj472S9r4",
                        "0eDvMgVFoNV3TpwtrVCoTj", "2pAWfrd7WFF3XhVt9GooDL",
                        "13ubrt8QOOCPljQ2FL1Kca", "1Xyo4u8uXC1ZmMpatF05PJ",
                        "4V8LLVI7PbaPR0K2TGSxFF", "1RyvyyTE3xzB2ZywiAwp0i"));


        ArrayList<ArtistDAO> finalArtistsData = artistsDatas[0];

        ArtistsGridViewAdapter adapter = new ArtistsGridViewAdapter(requireActivity(), finalArtistsData);
        adapter.setOnArtistClickListener(new OnArtistClickListener() {
            @Override
            public void onItemClick(ArtistDAO artist) {
                Bundle bundle = new Bundle();
                bundle.putString("artistUri", artist.Uri);
                bundle.putString("artistName", artist.Name);
                Navigation.findNavController(requireView()).navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            }
        });

        _binding.gvArtists.setAdapter(adapter);
        getArtists(artists, adapter);


        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





    }


    public void getArtists(List<String> ids, ArtistsGridViewAdapter adapter){
        ApiDataProvider api = new ApiDataProvider();
        api.getArtists(ids, new ArtistDataCallback() {
            @Override
            public void onArtistsDataReceived(ArrayList<ArtistDAO> artistsData) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        artistsDatas[0].clear();
                        artistsDatas[0].addAll(artistsData);
                        adapter.UpdateData(artistsData);
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