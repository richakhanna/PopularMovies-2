package com.richdroid.popularmovies.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.richdroid.popularmovies.R;
import com.richdroid.popularmovies.model.Movie;
import com.richdroid.popularmovies.ui.activity.MainActivity;
import com.richdroid.popularmovies.ui.activity.MovieDetailActivity;
import com.richdroid.popularmovies.ui.fragment.DetailFragment;
import com.richdroid.popularmovies.utils.PabloPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.richdroid.popularmovies.ui.activity.MovieDetailActivity.DETAIL_FRAGMENT_TAG;
import static com.richdroid.popularmovies.utils.Constants.ARG_MOVIE_DETAIL;

/**
 * Created by richa.khanna on 3/18/16.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private static List<Movie> mDatasetList;
    // Allows to remember the last item shown on screen
    private int lastAnimatedItemPosition = -1;
    private boolean mTwoPane;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieGridAdapter(Context context, List<Movie> datasetList, boolean twoPane) {
        mContext = context;
        mDatasetList = datasetList;
        mTwoPane = twoPane;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.tv_title)
        TextView mTVTitle;
        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.iv_thumbnail)
        ImageView mIVThumbNail;
        @BindView(R.id.tv_rating)
        TextView mTVRating;


        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.mIVThumbNail.setOnClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            if (itemPosition != RecyclerView.NO_POSITION) {
                Movie movie = mDatasetList.get(itemPosition);

                switch (view.getId()) {
                    case R.id.iv_thumbnail:

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARG_MOVIE_DETAIL, movie);

                        if (mTwoPane) {
                            addDetailFragmentForTwoPane(bundle);
                        } else {
                            Intent movieDetailIntent = new Intent(mContext, MovieDetailActivity.class);
                            movieDetailIntent.putExtras(bundle);
                            mContext.startActivity(movieDetailIntent);
                        }
                        break;

                    default:
                        Toast.makeText(mContext, "You clicked at position " + itemPosition +
                                " on movie thumbnail : " +
                                movie.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }

        }

        public void addDetailFragmentForTwoPane(Bundle bundle) {
            DetailFragment detailFragment = DetailFragment.newInstance(bundle);
            FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom,
                            android.support.design.R.anim.abc_shrink_fade_out_from_bottom)
                    .replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }

        @Override
        public boolean onLongClick(View v) {
            int itemPosition = getAdapterPosition();
            return true;
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_view, parent, false);
        return new MovieViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        MovieViewHolder cusHolder = (MovieViewHolder) holder;
        cusHolder.mTVTitle.setText(mDatasetList.get(position).getTitle());
        cusHolder.mTVRating.setText(String.valueOf(mDatasetList.get(position).getVoteAverage()));
        String completePosterPath = mDatasetList.get(position).getPosterPath();
        PabloPicasso.with(mContext).load(completePosterPath).
                placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .into(cusHolder.mIVThumbNail);
        cusHolder.mIVThumbNail.setVisibility(View.VISIBLE);
        setEnterAnimation(cusHolder.mCardView, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetList.size();
    }

    private void setEnterAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it will be animated
        if (position > lastAnimatedItemPosition) {
            //Animation using xml
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
            viewToAnimate.startAnimation(animation);
            lastAnimatedItemPosition = position;
        }
    }

    /**
     * The view could be reused while the animation is been happening.
     * In order to avoid that is recommendable to clear the animation when is detached.
     */
    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((MovieViewHolder) holder).mCardView.clearAnimation();
    }
}
