package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by alfriedl on 28.05.15.
 */
class DefaultErrorListener implements Response.ErrorListener {
    private final WebServiceCallback callback;

    DefaultErrorListener(WebServiceCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("DefaultErrorListener", "Network error", error);
        callback.onError(new WebServiceError("Web service communication error"));
    }
}
