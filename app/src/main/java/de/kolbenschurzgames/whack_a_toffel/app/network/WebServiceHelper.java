package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONException;
import org.json.JSONObject;

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
                new HighscoreResponseListener(callback),
                new DefaultErrorListener(callback));

        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void submitHighscore(final Highscore score, final WebServiceCallback<Highscore> callback) {
        try {
            final JSONObject jsonHighscore = score.toJSON();

            JsonArrayRequest highscorePostRequest = new JsonArrayRequest(Request.Method.POST, HIGHSCORES_URL,
                    jsonHighscore,
                    new HighscoreResponseListener(callback),
                    new DefaultErrorListener(callback));

            RequestQueueSingleton.getInstance(context).addToRequestQueue(highscorePostRequest);

        } catch (JSONException e) {
            callback.onError(new WebServiceError("Failed to parse highscore"));
        }
    }
}
