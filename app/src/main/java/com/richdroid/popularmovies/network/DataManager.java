package com.richdroid.popularmovies.network;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.richdroid.popularmovies.model.AllMovieResponse;
import com.richdroid.popularmovies.model.AllMovieReviewResponse;
import com.richdroid.popularmovies.model.AllVideoTrailerResponse;
import com.richdroid.popularmovies.utils.Constants;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by richa.khanna on 3/23/16.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    //Base Url for TMDB
    private static final String API_BASE_URL = "http://api.themoviedb.org/3";
    public static final String BASE_URL_IMAGE_POSTER = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_URL_IMAGE_BACKDROP = "http://image.tmdb.org/t/p/w780";

    public static String BASE_URL_VIDEO = "https://www.youtube.com/watch?v=";

    //Key to access TMDB
    private static final String API_KEY = "";
    private static DataManager mInstance;
    private Context mContext;
    private RequestQueue mRequestQueue;

    private DataManager(Context context) {
        mContext = context;
    }

    public static synchronized DataManager getInstance(Context context) {
        if (mInstance == null) {
            Log.v(TAG, "Creating data manager instance");
            mInstance = new DataManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void init() {
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    /**
     * Add the request with tag to volley request queue
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        // set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        mRequestQueue.add(request);
    }


    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        mRequestQueue.add(request);
    }

    /**
     * Cancel any pending volley request associated with the {param requestTag}
     */
    public void cancelPendingRequests(String requestTag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(requestTag);
        }
    }

    /**
     * Cleanup & save anything that needs saving as app is going away.
     */
    public void terminate() {
        mRequestQueue.stop();
    }


    /**
     * Get the list of movies from The Movie Database(tmdb). This list refreshes every day.
     */
    public void getMovies(final WeakReference<DataRequester> wRequester, String movieSortFilter, int page, String language, String tag) {
        Log.v(TAG, "Api call : get movies");
        JSONObject obj = new JSONObject();
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v(TAG, "Success : get movies returned a response");

                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                AllMovieResponse allMovieResponse = null;
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                    Log.v(TAG, "Success : converting Json to Java Object via Gson");
                    allMovieResponse =
                            new Gson().fromJson(jsonObject.toString(), AllMovieResponse.class);
                }

                if (req != null) {
                    if (allMovieResponse != null) {
                        req.onSuccess(allMovieResponse);
                    }
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(volleyError);
                }
            }
        };

        String toBeAppendedPath = null;
        if (movieSortFilter.equals(Constants.HIGHEST_RATED)) {
            toBeAppendedPath = "top_rated";
        } else if (movieSortFilter.equals(Constants.MOST_POPULAR)) {
            toBeAppendedPath = "popular";
        }

        Uri.Builder builder = Uri.parse(API_BASE_URL).buildUpon();
        builder.appendPath("movie").
                appendPath(toBeAppendedPath).
                appendQueryParameter("page", String.valueOf(page)).
                appendQueryParameter("language", language).
                appendQueryParameter("api_key", API_KEY);

        String url = builder.build().toString();

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.GET,
                url, obj, responseListener, errorListener);
        addToRequestQueue(request, tag);
    }

    /**
     * Get the videos (trailers, teasers, clips, etc...) for a specific movie id.
     *
     * @param movieId  TMDb id.
     * @param language Optional. ISO 639-1 code.
     */
    public void getVideoTrailers(final WeakReference<DataRequester> wRequester, int movieId, String language, String tag) {
        Log.v(TAG, "Api call : get video Trailers");
        JSONObject obj = new JSONObject();
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v(TAG, "Success : get video trailers returned a response");

                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                AllVideoTrailerResponse allVideoTrailerResponse = null;
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                    Log.v(TAG, "Success : converting Json to Java Object via Gson");
                    allVideoTrailerResponse =
                            new Gson().fromJson(jsonObject.toString(), AllVideoTrailerResponse.class);
                }

                if (req != null) {
                    if (allVideoTrailerResponse != null) {
                        req.onSuccess(allVideoTrailerResponse);
                    }
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(volleyError);
                }
            }
        };


        Uri.Builder builder = Uri.parse(API_BASE_URL).buildUpon();
        builder.appendPath("movie").
                appendPath(String.valueOf(movieId)).
                appendPath("videos").
                appendQueryParameter("language", language).
                appendQueryParameter("api_key", API_KEY);

        String url = builder.build().toString();

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.GET,
                url, obj, responseListener, errorListener);
        addToRequestQueue(request, tag);
    }

    /**
     * Get the reviews for a particular movie id.
     *
     * @param movieId  TMDb id.
     * @param language Optional. ISO 639-1 code.
     */
    public void getMovieReviews(final WeakReference<DataRequester> wRequester, int movieId, String language, String tag) {
        Log.v(TAG, "Api call : get Movie Reviews");
        JSONObject obj = new JSONObject();
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v(TAG, "Success : get movie reviews returned a response");

                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                AllMovieReviewResponse allMovieReviewResponse = null;
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                    Log.v(TAG, "Success : converting Json to Java Object via Gson");
                    allMovieReviewResponse =
                            new Gson().fromJson(jsonObject.toString(), AllMovieReviewResponse.class);
                }

                if (req != null) {
                    if (allMovieReviewResponse != null) {
                        req.onSuccess(allMovieReviewResponse);
                    }
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DataRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(volleyError);
                }
            }
        };

        Uri.Builder builder = Uri.parse(API_BASE_URL).buildUpon();
        builder.appendPath("movie").
                appendPath(String.valueOf(movieId)).
                appendPath("reviews").
                appendQueryParameter("language", language).
                appendQueryParameter("api_key", API_KEY);

        String url = builder.build().toString();

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.GET,
                url, obj, responseListener, errorListener);
        addToRequestQueue(request, tag);
    }


}
