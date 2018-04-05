package com.example.abhis.goldenstate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhis.goldenstate.constants.constant;
import com.example.abhis.goldenstate.database.SqliteHelper;
import com.example.abhis.goldenstate.modal.movieGeneral;
import com.example.abhis.goldenstate.modal.review.Results;
import com.example.abhis.goldenstate.modal.review.movieReview;
import com.example.abhis.goldenstate.modal.trailer.movieYoutubeModal;
import com.example.abhis.goldenstate.network.MovieAPI;
import com.example.abhis.goldenstate.network.NetworkAPI;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link ListActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {

    private FragmentManager fm;
    private movieGeneral moviegeneral;
    private TextView reviewText, titleText, voteText, peoplesText, calendarText, plotSynopsis;
    private ImageView titleImage;
    private LinearLayout youtubeViewHolder;
    private TextView shareYoutube;
    private String shareYoutubeID;
    private FloatingActionButton fab;

    public DetailFragment() {

    }

    public void setArgument(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        if (savedInstanceState != null) {
            this.moviegeneral = (movieGeneral) savedInstanceState.getSerializable("DATA");
        }
        updateGeneralUI(rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("DATA", moviegeneral);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setMovieData(movieGeneral moviegeneral) {
        this.moviegeneral = moviegeneral;
    }

    private void updateGeneralUI(View v) {
        titleText = (TextView) v.findViewById(R.id.titleText);
        voteText = (TextView) v.findViewById(R.id.rating);
        calendarText = (TextView) v.findViewById(R.id.calendar);
        peoplesText = (TextView) v.findViewById(R.id.people);
        titleImage = (ImageView) v.findViewById(R.id.titleimg);
        plotSynopsis = (TextView) v.findViewById(R.id.plotsynopsis);
        reviewText = (TextView) v.findViewById(R.id.reviewText);
        youtubeViewHolder = (LinearLayout) v.findViewById(R.id.youtubelayout);
        shareYoutube = (TextView) v.findViewById(R.id.youtubesharer);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        titleText.setText(moviegeneral.getTitle());
        voteText.setText(moviegeneral.getmVote());
        peoplesText.setText(moviegeneral.getmPeople());
        calendarText.setText(moviegeneral.getmReleaseDate());
        plotSynopsis.setText(moviegeneral.getmOverview());
        getMovieReview(reviewText);
        Picasso.with(getContext())
                .load(moviegeneral.getThumbnail())
                .into(titleImage);
        getMovieReview(reviewText);
        getTrailer(youtubeViewHolder);
        shareYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareYoutubeID != null) {
                    shareYoutubeIntent(shareYoutubeID);
                } else {
                    Toast.makeText(getContext(), "No Youtube Videos Available! Sorry", Toast.LENGTH_LONG).show();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });
    }

    protected void saveToDatabase() {
        SqliteHelper db = new SqliteHelper(getContext());
        if (!reviewText.getText().toString().contains("Sorry")) {
            moviegeneral.setmReview(reviewText.getText().toString());
        }
        boolean b = db.insertMovie(moviegeneral);
        if (b)
            Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "Seems Already in Favourites!", Toast.LENGTH_LONG).show();
    }
    protected void shareYoutubeIntent(String shareYoutubeID) {
        String url = "http://www.youtube.com/watch?v" + shareYoutubeID;
        String shareMsg = "hey,there new film named " + moviegeneral.getTitle() + " has been released and here is the Trailer link,Have a look at it " + url;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Movies Now - Android App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMsg);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    protected String generateYoutubeThumbnailURL(String id) {
        String url = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
        return url;
    }

    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    protected void getTrailer(final LinearLayout youtubeViewHolder) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchVideos(constant.ACCESS_TOKEN, this.moviegeneral.getmId(), new Callback<movieYoutubeModal>() {

            @Override
            public void success(movieYoutubeModal movieYoutubeModal, Response response) {
                youtubeViewHolder.setPadding(5, 10, 5, 0);
                com.example.abhis.goldenstate.modal.trailer.Results[] trailer = movieYoutubeModal.getResults();
                if (trailer.length > 0) {
                    shareYoutubeID = trailer[0].getKey();
                    for (final  com.example.abhis.goldenstate.modal.trailer.Results obj : trailer) {
                        String url = generateYoutubeThumbnailURL(obj.getKey());
                        ImageView myImage = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                180,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.leftMargin = 3;
                        params.rightMargin = 3;
                        params.topMargin = 6;
                        params.bottomMargin = 3;
                        myImage.setLayoutParams(params);
                        Picasso.with(getContext())
                                .load(url)
                                .into(myImage);
                        youtubeViewHolder.addView(myImage);
                        myImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                watchYoutubeVideo(obj.getKey());
                            }
                        });

                    }

                } else {
                    youtubeViewHolder.setPadding(50, 50, 50, 50);
                    TextView errmsg = new TextView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            30
                    );
                    errmsg.setLayoutParams(params);
                    errmsg.setText("That's Bad Luck,No Trailers Found!Check later");
                    youtubeViewHolder.addView(errmsg);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                youtubeViewHolder.setPadding(50, 50, 50, 50);
                TextView errmsg = new TextView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        30
                );
                errmsg.setLayoutParams(params);
                errmsg.setText("Network Error! You can't view Trailers Rite Now");
                youtubeViewHolder.addView(errmsg);

            }
        });
    }

    protected void getMovieReview(final View review) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchReview(constant.ACCESS_TOKEN, this.moviegeneral.getmId(), new Callback<movieReview>() {

            @Override
            public void success(movieReview movieReview, Response response) {
                Results[] movieResult = movieReview.getResults();
                if (movieResult.length > 0)
                    ((TextView) review).setText(movieResult[0].getContent());
                else
                    ((TextView) review).setText("Sorry No Review is Available Till Now!");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error", error.toString());
                ((TextView) review).setText("Sorry! Check Back Latter! Network Error!");
            }
        });
    }

    protected void generateThumbnail() {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}