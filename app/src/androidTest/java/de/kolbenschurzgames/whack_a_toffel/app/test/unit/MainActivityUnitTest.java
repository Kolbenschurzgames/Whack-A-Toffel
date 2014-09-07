package de.kolbenschurzgames.whack_a_toffel.app.test.unit;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.MainActivity;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;

/**
 * Created by alfriedl on 17.08.14.
 */
public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {

	private MainActivity mainActivity;
	private Button startButton;
	private Intent launchIntent;

	public MainActivityUnitTest() {
		this(MainActivity.class);
	}

	public MainActivityUnitTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		launchIntent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
		startActivity(launchIntent, null, null);
		mainActivity = getActivity();
		startButton = (Button) mainActivity.findViewById(R.id.button_start_game);
	}

	@SmallTest
	public void testStartButtonLaunchesGameActivityWithIntent() {
		startButton.performClick();

		final Intent startGameIntent = getStartedActivityIntent();
		assertNull("Intent was null", startGameIntent);
		assertEquals(GameActivity.class.getCanonicalName(), startGameIntent.getComponent().getClassName());
		assertFalse(isFinishCalled());
	}
}
