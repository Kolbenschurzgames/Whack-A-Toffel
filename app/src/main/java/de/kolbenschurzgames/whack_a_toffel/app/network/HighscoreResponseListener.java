package de.kolbenschurzgames.whack_a_toffel.app.network;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;

/**
 * Created by alfriedl on 28.05.15.
 */
class HighscoreResponseListener<T> implements Response.Listener<T> {

    private final WebServiceCallback<Highscore> callback;

    HighscoreResponseListener(WebServiceCallback<Highscore> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(T response) {
        try {
            if (response instanceof JSONArray) {
                List<Highscore> highscores = Highscore.parseJsonArrayToList((JSONArray) response);
                callback.onResultListReceived(highscores);
            } else if (response instanceof JSONObject) {
                Highscore highscore = Highscore.parseJsonObjectToHighscore((JSONObject) response);
                callback.onResultListReceived(Collections.singletonList(highscore));
            } else {
                callback.onError(new WebServiceError("Failed to parse response payload"));
            }
        } catch (JSONException e) {
            callback.onError(new WebServiceError("Failed to parse response payload to highscore list", e));
        }
    }
}
