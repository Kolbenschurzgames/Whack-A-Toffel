package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.app.Activity;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringRes;

/**
 * Created by alfriedl on 16.02.15.
 */
@EActivity(R.layout.activity_submit_highscore)
public class SubmitHighscoreActivity extends Activity {
	
	@Extra
	int score;
	
	@ViewById(R.id.dialog_highscore_text)
	TextView highscoresText;

	@StringRes(R.string.score)
	String scoreString;
	
	@AfterViews
	void initDialog() {
		setFinishOnTouchOutside(false);
		highscoresText.setText(scoreString + ": " + score);
	}
	
	@Click(R.id.button_submit)
	void onSubmitClicked() {
		launchHighscoreActivity();
	}
	
	@Click(R.id.button_skip)
	void onSkipClicked() {
		launchHighscoreActivity();
	}
	
	private void launchHighscoreActivity() {
		HighscoreActivity_.intent(SubmitHighscoreActivity.this).start();
	}
}
