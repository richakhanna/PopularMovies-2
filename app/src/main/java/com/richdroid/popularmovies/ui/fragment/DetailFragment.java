package com.richdroid.popularmovies.ui.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.richdroid.popularmovies.R;
import com.richdroid.popularmovies.app.AppController;
import com.richdroid.popularmovies.data.UpdateFavouriteMovieDBTask;
import com.richdroid.popularmovies.interfaces.DBUpdateListener;
import com.richdroid.popularmovies.model.AllMovieReviewResponse;
import com.richdroid.popularmovies.model.AllVideoTrailerResponse;
import com.richdroid.popularmovies.model.Language;
import com.richdroid.popularmovies.model.Movie;
import com.richdroid.popularmovies.model.MovieReview;
import com.richdroid.popularmovies.model.VideoTrailer;
import com.richdroid.popularmovies.network.DataManager;
import com.richdroid.popularmovies.network.DataRequester;
import com.richdroid.popularmovies.ui.activity.MainActivity;
import com.richdroid.popularmovies.utils.AlertDialogUtil;
import com.richdroid.popularmovies.utils.NetworkUtils;
import com.richdroid.popularmovies.utils.PabloPicasso;
import com.richdroid.popularmovies.utils.ProgressBarUtil;
import com.richdroid.popularmovies.utils.SPManagerFavMovies;
import com.squareup.picasso.Callback;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.richdroid.popularmovies.data.UpdateFavouriteMovieDBTask.ADDED_TO_FAVORITE;
import static com.richdroid.popularmovies.utils.Constants.ARG_MOVIE_DETAIL;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements View.OnClickListener, DBUpdateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAVE_ALL_MOVIE_REVIEWS_LIST = "ALL_MOVIE_REVIEWS_LIST";

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolBar;
    @BindView(R.id.iv_backdrop)
    ImageView mIvBackDrop;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_release)
    TextView mTvReleaseDate;
    @BindView(R.id.tv_rating)
    TextView mTvRating;
    @BindView(R.id.tv_movie_overview)
    TextView mTvOverview;
    @BindView(R.id.iv_poster)
    ImageView mIvPoster;
    @BindView(R.id.fab_favorite)
    FloatingActionButton mButtonFavorite;
    @BindColor(R.color.colorPrimaryDark)
    int primaryDark;
    @BindView(R.id.fab_trailer)
    FloatingActionButton mButtonTrailer;
    @BindView(R.id.fab_share)
    FloatingActionButton mButtonShare;
    @BindView(R.id.review_layout0)
    CardView mReviewLayout0;
    @BindView(R.id.review_layout1)
    CardView mReviewLayout1;


    @BindString(R.string.no_internet_connection)
    String noInternetConnection;
    @BindString(R.string.something_went_wrong)
    String somethingWentWrong;
    @BindString(R.string.movie_trailers_dialog_title)
    String movieTrailerDialogTitle;
    @BindString(R.string.no_internet_connection_to_show_reviews)
    String noInternetConnectionToShowReviews;

    private SPManagerFavMovies mSPManagerFavMovies;
    private Movie mMovie;
    private ProgressBarUtil mProgressBar;
    private DataManager mDataMan;
    private int mViewId;
    private Activity mActivity;
    private boolean mTwoPane;
    private ArrayList<MovieReview> mMovieReviewList;

    public static DetailFragment newInstance(Bundle args) {
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle arg = getArguments();
            mMovie = arg.getParcelable(ARG_MOVIE_DETAIL);

            Log.d(TAG, "Received movie from getArguments() :  " + mMovie.toString());
        }

        mActivity = getActivity();
        mProgressBar = new ProgressBarUtil(mActivity);

        AppController app = ((AppController) mActivity.getApplication());
        mDataMan = app.getDataManager();
        mSPManagerFavMovies = SPManagerFavMovies.getInstance(mActivity);

        if (NetworkUtils.isOnline(mActivity)) {
            mProgressBar.show();
            Log.v(TAG, "Calling : get movie reviews api");
            mDataMan.getMovieReviews(
                    new WeakReference<DataRequester>(mMovieReviewRequester), mMovie.getId(),
                    Language.LANGUAGE_EN.getValue(), TAG);

        } else {
            NetworkUtils.showSnackbar(mCollapsingToolBar, noInternetConnectionToShowReviews);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        if (getActivity().findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }
        setStatusBarColor(primaryDark);
        mCollapsingToolBar.setTitle(mMovie.getOriginalTitle());
        mCollapsingToolBar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mTvTitle.setText(mMovie.getOriginalTitle());

        String sourceDateStr = mMovie.getReleaseDate();
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Date sourceDate = null;
        try {
            sourceDate = sourceDateFormat.parse(sourceDateStr);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        SimpleDateFormat finalDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        String finalDateStr = finalDateFormat.format(sourceDate);

        mTvReleaseDate.setText(finalDateStr);
        mTvRating.setText(String.valueOf(mMovie.getVoteAverage()));
        mTvOverview.setText(mMovie.getOverview());

        PabloPicasso.with(mActivity).load(mMovie.getPosterPath()).fit()
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .into(mIvPoster);

        PabloPicasso.with(mActivity).load(mMovie.getBackdropPath()).error(R.mipmap.placeholder).
                into(mIvBackDrop, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "applyPalette mTwoPane : " + mTwoPane);
                        if (!mTwoPane && isAdded()) {
                            Bitmap bitmap = ((BitmapDrawable) mIvBackDrop.getDrawable()).getBitmap();
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    applyPalette(palette);
                                }
                            });
                        }

                    }

                    @Override
                    public void onError() {

                    }
                });

        mButtonFavorite.setOnClickListener(this);
        mButtonTrailer.setOnClickListener(this);
        mButtonShare.setOnClickListener(this);

        //If fav movie exists in db, fill the heart
        //otherwise, keep it empty
        if (mSPManagerFavMovies.getBoolean(mMovie.getId())) {
            mButtonFavorite.setImageResource(R.mipmap.heart_filled);
        } else {
            mButtonFavorite.setImageResource(R.mipmap.heart_empty);
        }

        return view;
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        mCollapsingToolBar.setContentScrimColor(palette.getMutedColor(primary));
        mCollapsingToolBar.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        setStatusBarColor(palette.getDarkMutedColor(primaryDark));
    }

    private void setStatusBarColor(int darkMutedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkMutedColor);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_favorite:
                //Update favorite movie database accordingly
                //If movie exists in fav db, delete it, Otherwise save it in db
                UpdateFavouriteMovieDBTask favouriteMovieDBTask = new UpdateFavouriteMovieDBTask(mActivity, mMovie, this);
                favouriteMovieDBTask.execute();
                break;

            case R.id.fab_trailer:
                mViewId = R.id.fab_trailer;
                if (NetworkUtils.isOnline(mActivity)) {
                    mProgressBar.show();
                    Log.v(TAG, "Calling : get video trailer api");
                    mDataMan.getVideoTrailers(
                            new WeakReference<DataRequester>(mVideoTrailerRequester), mMovie.getId(),
                            Language.LANGUAGE_EN.getValue(), TAG);

                } else {
                    NetworkUtils.showSnackbar(mCollapsingToolBar, noInternetConnection);
                }

                break;

            case R.id.fab_share:
                mViewId = R.id.fab_share;
                if (NetworkUtils.isOnline(mActivity)) {
                    mProgressBar.show();
                    Log.v(TAG, "Calling : get video trailer api to share the first trailer");
                    mDataMan.getVideoTrailers(
                            new WeakReference<DataRequester>(mVideoTrailerRequester), mMovie.getId(),
                            Language.LANGUAGE_EN.getValue(), TAG);

                } else {
                    NetworkUtils.showSnackbar(mCollapsingToolBar, noInternetConnection);
                }
                break;

        }
    }

    @Override
    public void onSuccess(final int operationType) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String operation;
                if (operationType == ADDED_TO_FAVORITE) {
                    operation = "added to favorite";
                    mButtonFavorite.setImageResource(R.mipmap.heart_filled);
                    mSPManagerFavMovies.putBoolean(mMovie.getId(), true);
                } else {
                    operation = "removed from favorite";
                    mButtonFavorite.setImageResource(R.mipmap.heart_empty);
                    mSPManagerFavMovies.putBoolean(mMovie.getId(), false);
                }

                NetworkUtils.showSnackbar(mCollapsingToolBar, mMovie.getTitle() + " " + operation);
            }
        });
    }

    @Override
    public void onFailure() {
        NetworkUtils.showSnackbar(mCollapsingToolBar, mMovie.getTitle() + " " + somethingWentWrong);
    }


    private DataRequester mVideoTrailerRequester = new DataRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (!isAdded()) {
                return;
            }

            mProgressBar.hide();
            Log.v(TAG, "Failure : video trailer onFailure");
            NetworkUtils.showSnackbar(mCollapsingToolBar, noInternetConnection);
        }

        @Override
        public void onSuccess(Object respObj) {
            if (!isAdded()) {
                return;
            }


            Log.v(TAG, "Success : video trailer data : " + new Gson().toJson(respObj).toString());
            final AllVideoTrailerResponse response = (AllVideoTrailerResponse) respObj;


            if (response != null && response.getResults() != null && response.getResults().size() > 0) {
                final List<VideoTrailer> videoTrailerList = response.getResults();

                int noOfTrailers = videoTrailerList.size();
                String[] trailerNames = new String[noOfTrailers];
                for (int i = 0; i < noOfTrailers; i++) {
                    trailerNames[i] = videoTrailerList.get(i).getName();
                }
                switch (mViewId) {
                    case R.id.fab_trailer:
                        mProgressBar.hide();
                        AlertDialogUtil.createSingleChoiceItemsAlert(mActivity, movieTrailerDialogTitle,
                                trailerNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        playVideoTrailer(videoTrailerList.get(which).getKey());
                                        dialog.dismiss();
                                    }
                                });
                        break;
                    case R.id.fab_share:
                        shareVideoTrailer(videoTrailerList.get(0).getKey());
                        break;

                }
            } else {
                mProgressBar.hide();
            }


        }
    };

    private void playVideoTrailer(String key) {
        Uri videoUri = Uri.parse(DataManager.BASE_URL_VIDEO + key);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(videoUri);

        //We only start the activity if it resolves successfully
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't play video trailer for key: " + key);
        }
    }

    private void shareVideoTrailer(String key) {
        String videoExtraText = DataManager.BASE_URL_VIDEO + key;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //This flag help you in returning to your app after any app handled the share intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT, videoExtraText);

        Intent shareIntent = Intent.createChooser(intent, "Share trailer via");


        // We only start the activity if it resolves successfully
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mProgressBar.hide();
            startActivity(shareIntent);
        } else {
            mProgressBar.hide();
            Log.d(TAG, "Couldn't share Video Trailer for key: " + key);
        }
    }

    private DataRequester mMovieReviewRequester = new DataRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (!isAdded()) {
                return;
            }

            mProgressBar.hide();
            Log.v(TAG, "Failure : movie Reviews onFailure");
            NetworkUtils.showSnackbar(mCollapsingToolBar, noInternetConnectionToShowReviews);
        }

        @Override
        public void onSuccess(Object respObj) {
            if (!isAdded()) {
                return;
            }

            mProgressBar.hide();
            Log.v(TAG, "Success : movie reviews data : " + new Gson().toJson(respObj).toString());
            final AllMovieReviewResponse response = (AllMovieReviewResponse) respObj;


            if (response != null && response.getResults() != null && response.getResults().size() > 0) {

                mMovieReviewList = response.getResults();

                int noOfReviews = mMovieReviewList.size();

                if (noOfReviews >= 2) {
                    //We can fill two reviews
                    displayReviewLayout(0, mMovieReviewList.get(0));
                    displayReviewLayout(1, mMovieReviewList.get(1));
                } else {
                    //We can fill only one review
                    displayReviewLayout(0, mMovieReviewList.get(0));
                }
            }
        }
    };

    private void displayReviewLayout(int position, MovieReview movieReview) {
        CardView reviewLayout = null;
        if (position == 0) {
            reviewLayout = mReviewLayout0;
            ((TextView) reviewLayout.findViewById(R.id.tv_reviews_text)).setVisibility(View.VISIBLE);
            reviewLayout.findViewById(R.id.line_reviews_heading).setVisibility(View.VISIBLE);
        } else if (position == 1) {
            reviewLayout = mReviewLayout1;
        }

        if (reviewLayout != null) {
            reviewLayout.setVisibility(View.VISIBLE);
            String author = movieReview.getAuthor();
            String content = movieReview.getContent();

            ((TextView) reviewLayout.findViewById(R.id.tv_review_author)).setText(author);
            ((TextView) reviewLayout.findViewById(R.id.tv_review_content)).setText(content);
        }
    }
}

