package de.kolbenschurzgames.whack_a_toffel.app.test.functional;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.KeyEvent;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.MainActivity;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity;

/**
 * Created by alfriedl on 17.08.14.
 */
public class MainActivityFunctionalTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final long TIMEOUT_IN_MS = 3000;

	private Button startGameButton;
	private Button highscoresButton;

	public MainActivityFunctionalTest() {
		this(MainActivity.class);
	}

	public MainActivityFunctionalTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		startGameButton = (Button) getActivity().findViewById(R.id.button_start_game);
		highscoresButton = (Button) getActivity().findViewById(R.id.button_highscores);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		this.sendKeys(KeyEvent.KEYCODE_BACK);
	}

	@SmallTest
	public void testStartButtonLaunchesGameActivity() {
		Instrumentation.ActivityMonitor gameActivityMonitor = getInstrumentation().
				addMonitor(GameActivity.class.getName(), null, false);
		TouchUtils.clickView(this, startGameButton);
		assertActivityLaunched(gameActivityMonitor, GameActivity.class);
	}

	@SmallTest
	public void testHighscoresButtonLaunchesHighscoreActivity() {
		Instrumentation.ActivityMonitor highscoreActivityMonitor = getInstrumentation().
				addMonitor(HighscoreActivity.class.getName(), null, false);
		TouchUtils.clickView(this, highscoresButton);
		assertActivityLaunched(highscoreActivityMonitor, HighscoreActivity.class);
	}

	private void assertActivityLaunched(Instrumentation.ActivityMonitor monitor, Class<? extends Activity> activityClass) {
		Activity activity = monitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
		assertNotNull("Activity is null", activity);
		assertEquals("Monitor for " + activityClass.getCanonicalName() + " has not been called", 1, monitor.getHits());
		assertEquals("Activity is of wrong type", activityClass, activity.getClass());
		getInstrumentation().removeMonitor(monitor);
	}
}
