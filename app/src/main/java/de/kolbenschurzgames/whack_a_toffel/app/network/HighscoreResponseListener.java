package de.kolbenschurzgames.whack_a_toffel.app.network;

import com.android.volley.Response;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by alfriedl on 28.05.15.
 */
class HighscoreResponseListener implements Response.Listener<JSONArray> {

    private final WebServiceCallback<Highscore> callback;

    HighscoreResponseListener(WebServiceCallback<Highscore> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(JSONArray response) {
        try {
            List<Highscore> highscores = Highscore.parseJsonArrayToList(response);
            callback.onResultListReceived(highscores);
        } catch (JSONException e) {
            callback.onError(new WebServiceError("Failed to parse response payload to highscore list", e));
        }
    }
}
