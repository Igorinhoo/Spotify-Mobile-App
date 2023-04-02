package com.app.spotifyapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.spotifyapp.R;
import com.app.spotifyapp.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding _binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _binding = FragmentFirstBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ArrayList<String> artists = new ArrayList<String>(
                Arrays.asList("5K4W6rqBFWDnAN6FQUkS6x", "2YZyLoL8N0Wb9xBt1NhZWg",
                        "3nFkdlSjzX9mRTtwJOzDYB", "0Y5tJX1MQlPlqiwlOH1tJY",
                        "20qISvAhX20dpIbOOzGK3q", "3TVXtAsR1Inumwj472S9r4",
                        "0eDvMgVFoNV3TpwtrVCoTj", "2pAWfrd7WFF3XhVt9GooDL",
                        "13ubrt8QOOCPljQ2FL1Kca", "1Xyo4u8uXC1ZmMpatF05PJ",
                        "4V8LLVI7PbaPR0K2TGSxFF"));
        List<String> buttons = Arrays.asList("Kanye West", "Kendrick Lamar", "Jay-Z",
                "Travis Scott", "Nas", "Drake", "Pop Smoke", "MF DOOM", "A$AP ROCKY", "The Weeknd", "Tyler, The Creator");

        AddViewButtons(buttons);
        for (int i = 0; i < buttons.size(); i++) {
            int finalI = i;
            Button button = (Button) getActivity().findViewById(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("artistUri", artists.get(finalI));
                    bundle.putString("artistName", buttons.get(finalI));
                    Navigation.findNavController(view).navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
                }
            });
        }
    }


    private void AddViewButtons(List<String> buttons){
        LinearLayout layout = requireActivity().findViewById(R.id.fragmentFirstContainer);
        for(int i = 0; i < buttons.size(); i++) {
            Button btn = new Button(getContext());
            btn.setText(buttons.get(i));
            btn.setId(i);
            layout.addView(btn);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}