package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.Listeners.OnArtistClickListener;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopArtistsRecyclerViewAdapter extends RecyclerView.Adapter<TopArtistsRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ArtistDAO> data;

//    private final OnArtistClickListener _listener;

    /*public TopArtistsRecyclerViewAdapter(Context context, ArrayList<ArtistDAO> data, OnArtistClickListener listener){
        this.context = context;
        this.data = data;
        this._listener = listener;
    }*/
    public TopArtistsRecyclerViewAdapter(Context context, ArrayList<ArtistDAO> data){
        this.context = context;
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


//        return new ViewHolder(view, _listener, data);
        return new ViewHolder(view, data);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            ArtistDAO artist = data.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("artistUri", artist.Id);
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




//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name, place;
        private final ImageView image;
//        private final OnArtistClickListener _listener;
        private final ArrayList<ArtistDAO> _data;

//        public ViewHolder(@NonNull View itemView, OnArtistClickListener listener, ArrayList<ArtistDAO> data) {
//            super(itemView);
//
//            name = itemView.findViewById(R.id.topArtistName);
//            image = itemView.findViewById(R.id.topArtistImg);
//            place = itemView.findViewById(R.id.topArtistPosition);
//
//            _listener = listener;
//            itemView.setOnClickListener(this);
//            _data = data;
//        }

       public ViewHolder(@NonNull View itemView, ArrayList<ArtistDAO> data) {
            super(itemView);

            name = itemView.findViewById(R.id.topArtistName);
            image = itemView.findViewById(R.id.topArtistImg);
            place = itemView.findViewById(R.id.topArtistPosition);

            _data = data;
        }

//        @Override
//        public void onClick(View view) {
//            _listener.onItemClick(_data.get(getAdapterPosition()));
//        }
    }

}
