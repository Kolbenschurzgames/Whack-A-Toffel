package de.kolbenschurzgames.whack_a_toffel.app;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.sound.TitleSound_;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 17.08.14.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({TitleSound_.class})
public class MainActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private MainActivity_ mainActivity;
    private TitleSound_ mockBackgroundSound;
    private ActivityController<MainActivity_> controller;

    @Before
    public void setUp() {
        mockStatic(TitleSound_.class);

        mockBackgroundSound = PowerMockito.mock(TitleSound_.class);
        when(TitleSound_.getInstance_(any(Context.class))).thenReturn(mockBackgroundSound);

        controller = buildActivity(MainActivity_.class).setup();

        mainActivity = controller.get();
        controller.start();
    }

    @After
    public void resetTest() {
        reset(mockBackgroundSound);
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

    @Test
    public void backgroundMusicPlayingOnActivityStart() {
        verify(mockBackgroundSound).start(any(Context.class));
    }

    @Test
    public void backgroundMusicPlayingOnResume() {
        controller.resume();
        verify(mockBackgroundSound, times(2)).start(any(Context.class));
    }

    @Test
    public void backgroundMusicPlayingOnPause() {
        controller.pause();
        verify(mockBackgroundSound).stop();
    }
}
