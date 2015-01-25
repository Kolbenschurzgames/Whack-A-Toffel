package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by alfriedl on 08.11.14.
 */
@EBean
public class WebServiceHelper {

	private static final String HIGHSCORES_URL = "http://10.0.2.2:3000/highscore";

	@RootContext
	Context context;

	public void getListOfHighscores(final WebServiceCallback<Highscore> callback) throws WebServiceError {
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(HIGHSCORES_URL,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						try {
							List<Highscore> highscores = Highscore.parseJsonArrayToList(response);
							callback.onResultListReceived(highscores);
						} catch (JSONException e) {
							callback.onError(new WebServiceError("Failed to parse response payload to highscore list", e));
						}
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						callback.onError(new WebServiceError("Web service communication error"));
					}
				});

		RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
	}
}
