package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.content.Intent;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 20.02.15.
 */
@RunWith(RobolectricTestRunner.class)
public class SubmitHighscoreActivityUnitTest {

	private SubmitHighscoreActivity_ submitHighscoreActivity;
	private int testScore = 100;
	
	@Before
	public void setUp() {
		Intent testIntent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), SubmitHighscoreActivity_.class);
		testIntent.putExtra("score", testScore);
		submitHighscoreActivity = buildActivity(SubmitHighscoreActivity_.class).withIntent(testIntent).create().get();
	}

	@Test
	public void initSetsHighscoreText() {
		Assert.assertEquals(Robolectric.application.getText(R.string.score) + ": " + testScore, submitHighscoreActivity.highscoresText.getText());
	}
	
	@Test
	public void submitButtonLaunchesHighscoreActivivity() {
		Button submitButton = (Button) submitHighscoreActivity.findViewById(R.id.button_submit);
		submitButton.performClick();

		assertHighscoreActivityLaunched();
	}
	
	@Test
	public void skipButtonLaunchesHighscoreActivivity() {
		Button skipButton = (Button) submitHighscoreActivity.findViewById(R.id.button_skip);
		skipButton.performClick();

		assertHighscoreActivityLaunched();
	}

	private void assertHighscoreActivityLaunched() {
		Intent expectedIntent = new Intent(submitHighscoreActivity, HighscoreActivity_.class);
		Assert.assertEquals(expectedIntent, shadowOf(submitHighscoreActivity).getNextStartedActivity());
	}
}
