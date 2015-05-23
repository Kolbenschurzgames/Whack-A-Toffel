package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.MainActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringRes;

import java.util.Date;
import java.util.List;

/**
 * Created by alfriedl on 16.02.15.
 */
@EActivity(R.layout.activity_submit_highscore)
public class SubmitHighscoreActivity extends Activity implements WebServiceCallback<Highscore> {

	@Extra
	int score;

	@Extra
	Date endOfGame;

	@ViewById(R.id.dialog_highscore_text)
	TextView highscoresText;

	@ViewById(R.id.username_text_input)
	EditText usernameTextInput;

	@StringRes(R.string.score)
	String scoreString;

	@StringRes(R.string.submitting_highscore)
	String submittingString;

	@StringRes(R.string.wait_a_moment)
	String waitString;

	@Bean
	WebServiceHelper webServiceHelper;

	private ProgressDialog progressDialog;

	@AfterViews
	void initDialog() {
		setFinishOnTouchOutside(false);
		highscoresText.setText(scoreString + ": " + score);
	}

	@Click(R.id.button_submit)
	void onSubmitClicked() {
		progressDialog = ProgressDialog.show(this, submittingString, waitString);
		submitHighscore();
	}

	@Background
	void submitHighscore() {
		Highscore highscore = buildHighscore();
		webServiceHelper.submitHighscore(highscore, this);
	}

	private Highscore buildHighscore() {
		String username = usernameTextInput.getText().toString();
		return new Highscore(username, this.score, this.endOfGame);
	}

	@Click(R.id.button_skip)
	void onSkipClicked() {
		launchMainActivity();
	}

	private void launchMainActivity() {
		MainActivity_.intent(SubmitHighscoreActivity.this).start();
	}

	@Override
	public void onResultListReceived(List<Highscore> results) {
		launchHighscoreActivity();
	}

	@Override
	public void onError(Error e) {
		Log.e("submit highscore", "JSON parse exception", e);
		launchHighscoreActivity();
	}

	@UiThread
	void launchHighscoreActivity() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		;
		HighscoreActivity_.intent(SubmitHighscoreActivity.this).start();
	}
}
