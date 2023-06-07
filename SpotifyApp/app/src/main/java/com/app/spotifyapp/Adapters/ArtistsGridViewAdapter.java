package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.gridmodel, parent, false);
        }
        ArtistDAO courseModel = null;
        if (data != null) {
            courseModel = data.get(position);
        }
        TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);

        ArtistDAO finalCourseModel = courseModel;
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artistClickListener != null) {
                    artistClickListener.onItemClick(finalCourseModel);
                }
            }
        });

        if (courseModel != null) {

            courseTV.setText(courseModel.Name);
            Picasso.get().load(courseModel.Img).into(courseIV);
        }
        return listitemView;
    }
}