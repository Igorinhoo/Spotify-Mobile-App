package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

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
        listitemView.setOnClickListener(v -> {
            if (artistClickListener != null) {
                artistClickListener.onItemClick(finalCourseModel);
            }
        });

        LinearLayout llBackground = listitemView.findViewById(R.id.cardViewLinear);
        listitemView.setOnLongClickListener(new View.OnLongClickListener() {
            private final Handler longClickHandler = new Handler(Looper.getMainLooper());
            @Override
            public boolean onLongClick(View view) {
                llBackground.setBackgroundResource(R.color.touched);
                longClickHandler.postDelayed(() -> {
                    if (artistLongClickListener != null) {
                        artistLongClickListener.onItemClick(finalCourseModel);
                        llBackground.setBackgroundResource(R.color.basic);
                    }
                }, 1000);
                return true;
            }
        });


        if (courseModel != null) {

            courseTV.setText(courseModel.Name);
            Picasso.get().load(courseModel.Img).into(courseIV);
        }
        return listitemView;
    }
}