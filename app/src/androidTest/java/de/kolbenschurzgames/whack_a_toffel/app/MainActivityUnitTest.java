package de.kolbenschurzgames.whack_a_toffel.app;

import android.content.Intent;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 17.08.14.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityUnitTest {

	private MainActivity_ mainActivity;

	@Before
	public void setUp() {
		mainActivity = setupActivity(MainActivity_.class);
	}

	@Test
	public void testStartButtonLaunchesGameActivityWithIntent() {
		Button startButton = (Button) mainActivity.findViewById(R.id.button_start_game);
		startButton.performClick();

		Intent expectedIntent = new Intent(mainActivity, GameActivity_.class);
		Assert.assertEquals(expectedIntent, shadowOf(mainActivity).getNextStartedActivity());
	}

	@Test
	public void testHighscoresButtonLaunchesHighscoresActivityWithIntent() {
		Button highscoresButton = (Button) mainActivity.findViewById(R.id.button_highscores);
		highscoresButton.performClick();

		Intent expectedIntent = new Intent(mainActivity, HighscoreActivity_.class);
		Assert.assertEquals(expectedIntent, shadowOf(mainActivity).getNextStartedActivity());
	}
}
