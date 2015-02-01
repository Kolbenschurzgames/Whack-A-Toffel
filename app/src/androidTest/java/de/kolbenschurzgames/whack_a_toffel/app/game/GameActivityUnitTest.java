package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Intent;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowCountDownTimer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 25.01.15.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public class GameActivityUnitTest {

	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	private GameActivity_ gameActivity;

	@Before
	public void setUp() throws Exception {
		gameActivity = setupActivity(GameActivity_.class);
	}

	@Test
	public void testHighscoreActivityLaunchedAfterTimerExpired() {
		ShadowCountDownTimer shadowCountDownTimer = shadowOf(gameActivity.countDownTimer);
		ShadowCountDownTimer countDownTimerSpy = Mockito.spy(shadowCountDownTimer);

		gameActivity.initCountdownTimer();
		verify(countDownTimerSpy, times(1)).start();

		shadowCountDownTimer.invokeFinish();
		Intent expectedIntent = new Intent(gameActivity, HighscoreActivity_.class);
		Assert.assertEquals(expectedIntent, shadowOf(gameActivity).getNextStartedActivity());
	}
}
