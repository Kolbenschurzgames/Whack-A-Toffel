package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.NetworkUtils;
import de.kolbenschurzgames.whack_a_toffel.app.network.RequestQueueSingleton;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by alfriedl on 19.09.14.
 */
public class HighscoreActivity extends Activity {

	private final String HIGHSCORES_URL = "http://10.0.2.2:3000/highscore";

	private TextView textView;
	private TableLayout highscoresTable;

	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscores);

		textView = (TextView) findViewById(R.id.highscores_text_view);
		highscoresTable = (TableLayout) findViewById(R.id.highscores_table);

		inflater = getLayoutInflater();

		if (NetworkUtils.isConnectionAvailable(this)) {
			fetchHighscores();
		} else {
			textView.setText(getString(R.string.no_connection));
		}
	}

	private void fetchHighscores() {
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(HIGHSCORES_URL,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						textView.setVisibility(View.INVISIBLE);
						highscoresTable.setVisibility(View.VISIBLE);
						try {
							List<Highscore> highscores = Highscore.parseJsonArrayToList(response);
							displayHighscores(highscores);
						} catch (JSONException e) {
							displayError();
						}
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						displayError();
					}
				});

		RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
	}

	private void displayHighscores(List<Highscore> highscores) {
		for (int i = 0; i < highscores.size(); i++) {
			Highscore highscore = highscores.get(i);
			TableRow row = buildHighscoreTableRow(highscore);
			highscoresTable.addView(row, i + 1);
		}
	}

	private TableRow buildHighscoreTableRow(Highscore highscore) {
		TableRow row = (TableRow) inflater.inflate(R.layout.table_row_highscore, null);
		((TextView) row.getChildAt(0)).setText(highscore.getName());
		((TextView) row.getChildAt(1)).setText(Integer.toString(highscore.getScore()));
		((TextView) row.getChildAt(2)).setText(getLocalizedDateString(highscore.getDate()));
		return row;
	}

	private String getLocalizedDateString(Date date) {
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		String dateString = dateFormat.format(date);
		String timeString = timeFormat.format(date);
		return dateString + " " + timeString;
	}

	private void displayError() {
		String errorMsg = getString(R.string.load_highscores_error);
		highscoresTable.setVisibility(View.INVISIBLE);
		textView.setText(errorMsg);
		textView.setVisibility(View.VISIBLE);
	}
}
