package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.OnAlbumClickListener;
import com.app.spotifyapp.Interfaces.OnArtistClickListener;
import com.app.spotifyapp.Interfaces.OnTopArtistClickListener;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopArtistsRecyclerViewAdapter extends RecyclerView.Adapter<TopArtistsRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ArtistDAO> data;

    private OnTopArtistClickListener _listener;

    public TopArtistsRecyclerViewAdapter(Context context, ArrayList<ArtistDAO> data, OnTopArtistClickListener listener){
        this.context = context;
        this.data = data;
        this._listener = listener;
    }

    public void UpdateData(ArrayList<ArtistDAO> newData){
        this.data = newData;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopArtistsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_artists,
                parent,
                false);


        return new ViewHolder(view, _listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    _listener.onItemClick(position);
                }
            }
        });
        holder.place.setText(Integer.toString(position+1));
        holder.name.setText(data.get(position).Name);
        Picasso.get().load(data.get(position).Img).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, place;
        private ImageView image;
        private final OnTopArtistClickListener _listener;

        public ViewHolder(@NonNull View itemView, OnTopArtistClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.topArtistName);
            image = itemView.findViewById(R.id.topArtistImg);
            place = itemView.findViewById(R.id.topArtistPosition);

            _listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            _listener.onItemClick(getAdapterPosition());
        }
    }

}
