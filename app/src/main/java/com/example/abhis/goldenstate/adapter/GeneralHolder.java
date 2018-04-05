package com.example.abhis.goldenstate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhis.goldenstate.R;

/**
 * Created by abhis on 3/31/2018.
 */

public class GeneralHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView movieNaming, movieAvging;
    public ImageView moviePhoto;
    public View mView;

    public GeneralHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mView = itemView;
        movieNaming = (TextView) itemView.findViewById(R.id.movieName);
        movieAvging = (TextView) itemView.findViewById(R.id.vote);
        moviePhoto = (ImageView) itemView.findViewById(R.id.moviePhoto);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }
}
