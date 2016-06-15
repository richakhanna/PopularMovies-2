package com.richdroid.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by richa.khanna on 8/8/15.
 */

public class ProgressBarUtil {
    private final RelativeLayout rl;

    public ProgressBarUtil(Context context) {
        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        rl = new RelativeLayout(context);

        rl.setGravity(Gravity.CENTER);
        rl.addView(progressBar);
        layout.addView(rl, params);

        hide();
    }

    public void show() {
        rl.setVisibility(View.VISIBLE);
    }

    public void hide() {
        rl.setVisibility(View.GONE);
    }
}

