package com.richdroid.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.richdroid.popularmovies.R;
import com.richdroid.popularmovies.ui.fragment.DetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        } else {
            // Add the Detail Fragment to the 'detail_container' FrameLayout
            addDetailFragment();
        }
    }

    public void addDetailFragment() {
        if (!isFinishing()) {
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            DetailFragment detailFragment = DetailFragment.newInstance(getIntent().getExtras());

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }
}
