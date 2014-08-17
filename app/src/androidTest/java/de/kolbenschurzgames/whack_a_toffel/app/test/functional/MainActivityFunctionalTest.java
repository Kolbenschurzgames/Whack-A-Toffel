package de.kolbenschurzgames.whack_a_toffel.app.test.functional;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.MainActivity;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;

/**
 * Created by alfriedl on 17.08.14.
 */
public class MainActivityFunctionalTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final long TIMEOUT_IN_MS = 2000;

	private Instrumentation.ActivityMonitor receiverActivityMonitor;

	private Button startGameButton;

	public MainActivityFunctionalTest() {
		this(MainActivity.class);
	}

	public MainActivityFunctionalTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		receiverActivityMonitor = getInstrumentation().addMonitor(GameActivity.class.getName(),
				null, false);
		startGameButton = (Button) getActivity().findViewById(R.id.button_start_game);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		getInstrumentation().removeMonitor(receiverActivityMonitor);
	}

	@SmallTest
	public void testStartButtonLaunchesGameActivity() {
		TouchUtils.clickView(this, startGameButton);
		GameActivity gameActivity = (GameActivity)
				receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
		assertNotNull("GameActivity is null", gameActivity);
		assertEquals("Monitor for GameActivity has not been called",
				1, receiverActivityMonitor.getHits());
		assertEquals("Activity is of wrong type",
				GameActivity.class, gameActivity.getClass());
	}
}
