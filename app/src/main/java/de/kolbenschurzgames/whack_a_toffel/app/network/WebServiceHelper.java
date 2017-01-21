package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;

/**
 * Created by alfriedl on 08.11.14.
 */
@EBean
public class WebServiceHelper {

    //private static final String HIGHSCORES_URL = "http://10.0.2.2:3000/highscore";
    private static final String HIGHSCORES_URL = "https://tap-a-toffel.herokuapp.com/highscore";

    @RootContext
    Context context;

    public void getListOfHighscores(final WebServiceCallback<Highscore> callback) throws WebServiceError {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(HIGHSCORES_URL,
                new HighscoreResponseListener<JSONArray>(callback),
                new DefaultErrorListener(callback));

        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void submitHighscore(final Highscore score, final WebServiceCallback<Highscore> callback) {
        try {
            final JSONObject jsonHighscore = score.toJSON();

            JsonObjectRequest highscorePostRequest = new JsonObjectRequest(Request.Method.POST, HIGHSCORES_URL,
                    jsonHighscore,
                    new HighscoreResponseListener<JSONObject>(callback),
                    new DefaultErrorListener(callback));

            RequestQueueSingleton.getInstance(context).addToRequestQueue(highscorePostRequest);

        } catch (JSONException e) {
            callback.onError(new WebServiceError("Failed to parse highscore"));
        }
    }
}
