package com.richdroid.popularmovies.network;

/**
 * Represents an interface that should be implemented by clients that ask for Data from {@code
 * DataManager}
 */
public interface DataRequester {

    /**
     * Fetch request failed.
     */
    void onFailure(Throwable error);

    /**
     * Fetch request succeeded.
     *
     * @param respObj POJO created via GSON from response JSON
     */
    void onSuccess(Object respObj);
}
