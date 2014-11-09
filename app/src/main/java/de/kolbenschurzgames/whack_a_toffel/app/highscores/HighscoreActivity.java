package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.NetworkUtils;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by alfriedl on 19.09.14.
 */
public class HighscoreActivity extends Activity implements WebServiceCallback<Highscore> {

	private TextView textView;
	private TableLayout highscoresTable;

	private LayoutInflater inflater;

	private WebServiceHelper webServiceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscores);

		textView = (TextView) findViewById(R.id.highscores_text_view);
		highscoresTable = (TableLayout) findViewById(R.id.highscores_table);

		inflater = getLayoutInflater();

		webServiceHelper = new WebServiceHelper(this);

		if (NetworkUtils.isConnectionAvailable(this)) {
			webServiceHelper.getListOfHighscores(this);
		} else {
			textView.setText(getString(R.string.no_connection));
		}
	}

	@Override
	public void onResultListReceived(List<Highscore> highscores) {
		textView.setVisibility(View.INVISIBLE);
		highscoresTable.setVisibility(View.VISIBLE);
		displayHighscores(highscores);
	}

	@Override
	public void onError(Error e) {
		Log.e("Highscores", e.getMessage(), e);
		displayError();
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
		((TextView) row.getChildAt(2)).setText(buildLocalizedDateTimeString(highscore.getDate()));
		return row;
	}

	private String buildLocalizedDateTimeString(Date date) {
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
