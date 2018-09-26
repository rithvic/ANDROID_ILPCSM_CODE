package com.qbay.qbayApps.ilpcsm.Services;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by benchmark on 13/03/18.
 */

public class AppController extends Application {

    private RequestQueue mRequestQueue;
    public static final String TAG = AppController.class
            .getSimpleName();

    private static AppController ourInstance;

    public static AppController getInstance() {
        return ourInstance;
    }

    public AppController(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
