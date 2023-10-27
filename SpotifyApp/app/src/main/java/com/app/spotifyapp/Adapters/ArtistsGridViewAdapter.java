package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.spotifyapp.Interfaces.Listeners.OnArtistClickListener;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistsGridViewAdapter extends ArrayAdapter<ArtistDAO> {

    private ArrayList<ArtistDAO> data;
    private OnArtistClickListener artistClickListener;
    private OnArtistClickListener artistLongClickListener;

    public ArtistsGridViewAdapter(@NonNull Context context, ArrayList<ArtistDAO> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
        }

    public void UpdateData(ArrayList<ArtistDAO> newData){
        this.data = newData;
        this.notifyDataSetChanged();
    }
    public void setOnArtistClickListener(OnArtistClickListener listener) {
        this.artistClickListener = listener;
    }
    public void setOnArtistLongClickListener(OnArtistClickListener listener){
        artistLongClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.gridmodel, parent, false);
        }
        ArtistDAO artistModel = null;
        if (data != null) {
            artistModel = data.get(position);
        }
        TextView artistName = itemView.findViewById(R.id.tvArtistName);
        ImageView artistImage = itemView.findViewById(R.id.ivArtistImage);

        ArtistDAO finalArtistModel = artistModel;
        itemView.setOnClickListener(v -> {
            if (artistClickListener != null) {
                artistClickListener.onItemClick(finalArtistModel);
            }
        });

        LinearLayout llBackground = itemView.findViewById(R.id.cardViewLinear);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            private final Handler longClickHandler = new Handler(Looper.getMainLooper());
            @Override
            public boolean onLongClick(View view) {
                llBackground.setBackgroundResource(R.color.touched);
                longClickHandler.postDelayed(() -> {
                    if (artistLongClickListener != null) {
                        artistLongClickListener.onItemClick(finalArtistModel);
                        llBackground.setBackgroundResource(R.color.basic);
                    }
                }, 1000);
                return true;
            }
        });


        if (artistModel != null) {
            artistName.setText(artistModel.Name);
            Picasso.get().load(artistModel.Img).into(artistImage);
        }
        return itemView;
    }
}