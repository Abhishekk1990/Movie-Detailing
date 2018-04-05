package com.example.abhis.goldenstate.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhis.goldenstate.R;
import com.example.abhis.goldenstate.modal.movieGeneral;
import com.example.abhis.goldenstate.DetailActivity;
import com.example.abhis.goldenstate.DetailFragment;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by abhis on 3/31/2018.
 */

public class GeneralAdapter extends RecyclerView.Adapter<GeneralHolder> {

    private List<movieGeneral> mMovieGenerals;
    private Context context;
    private boolean mTwoPanede;
    private FragmentManager fmm;

    public GeneralAdapter(Context context, List<movieGeneral> itemList, boolean mTwoPanede, FragmentManager fmm) {
        this.mMovieGenerals = itemList;
        this.context = context;
        this.mTwoPanede = mTwoPanede;
        this.fmm = fmm;
    }

    @Override
    public GeneralHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_cards, null);
        GeneralHolder rcv = new GeneralHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(GeneralHolder holder, final int position) {
        holder.movieNaming.setText(mMovieGenerals.get(position).getTitle());
        holder.movieAvging.setText(mMovieGenerals.get(position).getmVote());
        //picasso loading here
        Picasso.with(context)
                .load(mMovieGenerals.get(position).getThumbnail())
                .into(holder.moviePhoto);
        if (position == 0 && mTwoPanede) {
            DetailFragment fragment = new DetailFragment();
            fragment.setMovieData(mMovieGenerals.get(0));
            fragment.setArgument(fmm);
            fmm
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPanede) {
                    DetailFragment fragment = new DetailFragment();
                    fragment.setMovieData(mMovieGenerals.get(position));
                    fragment.setArgument(fmm);
                    fmm
                            .beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("DATA_MOVIE", mMovieGenerals.get(position));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mMovieGenerals.size();
    }
}
