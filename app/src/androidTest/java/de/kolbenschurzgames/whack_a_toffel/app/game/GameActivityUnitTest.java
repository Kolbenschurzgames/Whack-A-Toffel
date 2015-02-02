package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Intent;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowCountDownTimer;

import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 25.01.15.
 */
@RunWith(RobolectricTestRunner.class)
public class GameActivityUnitTest {

	private GameActivity_ gameActivity;
	private ShadowCountDownTimer shadowCountDownTimer;

	@Before
	public void setUp() {
		gameActivity = setupActivity(GameActivity_.class);
		shadowCountDownTimer = shadowOf(gameActivity.countDownTimer);
	}

	@Test
	public void testInitStartsTimer() {
		Assert.assertTrue(shadowCountDownTimer.hasStarted());
	}

	@Test
	public void testTimerUpdateForwardedToGameView() {
		shadowCountDownTimer.invokeTick(3000);
		Assert.assertEquals("3", gameActivity.gameView.getCurrentTimerValue());
	}

	@Test
	public void testHighscoreActivityLaunchedAfterTimerExpired() {
		shadowCountDownTimer.invokeFinish();
		Intent expectedIntent = new Intent(gameActivity, HighscoreActivity_.class);
		Assert.assertEquals(expectedIntent, shadowOf(gameActivity).getNextStartedActivity());
	}
}
