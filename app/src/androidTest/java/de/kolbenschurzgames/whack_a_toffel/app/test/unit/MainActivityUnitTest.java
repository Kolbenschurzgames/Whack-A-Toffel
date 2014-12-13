package de.kolbenschurzgames.whack_a_toffel.app.test.unit;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.MainActivity;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity;

/**
 * Created by alfriedl on 17.08.14.
 */
public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {

	private MainActivity mainActivity;

	private Button startButton;
	private Button highscoresButton;

	public MainActivityUnitTest() {
		this(MainActivity.class);
	}

	public MainActivityUnitTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Intent mainMenuIntent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
		startActivity(mainMenuIntent, null, null);
		mainActivity = getActivity();

		startButton = (Button) mainActivity.findViewById(R.id.button_start_game);
		highscoresButton = (Button) mainActivity.findViewById(R.id.button_highscores);
	}

	@SmallTest
	public void testStartButtonLaunchesGameActivityWithIntent() {
		startButton.performClick();

		final Intent startGameIntent = getStartedActivityIntent();
		assertNotNull("StartGame Intent was null", startGameIntent);
		assertEquals(GameActivity.class.getCanonicalName(), startGameIntent.getComponent().getClassName());
		assertFalse(isFinishCalled());
	}

	@SmallTest
	public void testHighscoresButtonLaunchesHighscoresActivityWithIntent() {
		highscoresButton.performClick();

		final Intent highscoresIntent = getStartedActivityIntent();
		assertNotNull("Highscores intent was null", highscoresIntent);
		assertEquals(HighscoreActivity.class.getCanonicalName(), highscoresIntent.getComponent().getClassName());
		assertFalse(isFinishCalled());
	}
}
