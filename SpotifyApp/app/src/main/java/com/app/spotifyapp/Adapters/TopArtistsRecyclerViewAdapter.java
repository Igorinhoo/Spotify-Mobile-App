package com.app.spotifyapp.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopArtistsRecyclerViewAdapter extends RecyclerView.Adapter<TopArtistsRecyclerViewAdapter.ViewHolder>{
    private ArrayList<ArtistDAO> data;

    public TopArtistsRecyclerViewAdapter(ArrayList<ArtistDAO> data){
        this.data = data;
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


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            ArtistDAO artist = data.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("artistID", artist.Id);
            bundle.putString("artistName", artist.Name);
            bundle.putString("artistImg", artist.Img);

            Navigation.findNavController(view).navigate(R.id.action_statistics_to_artistAlbumsFragment, bundle);
        });


        holder.place.setText(String.valueOf(position+1));
        holder.name.setText(data.get(position).Name);
        Picasso.get().load(data.get(position).Img).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name, place;
        private final ImageView image;

       public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.topArtistName);
            image = itemView.findViewById(R.id.topArtistImg);
            place = itemView.findViewById(R.id.topArtistPosition);

        }

    }

}
