package de.kolbenschurzgames.whack_a_toffel.app;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.sound.TitleSound_;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.buildActivity;

/**
 * Created by alfriedl on 17.08.14.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({TitleSound_.class})
public class MainActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private MainActivity_ mainActivity;
    private ActivityController<MainActivity_> controller;

    @Mock
    private TitleSound_ mockBackgroundSound;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockStatic(TitleSound_.class);

        when(TitleSound_.getInstance_(any(Context.class))).thenReturn(mockBackgroundSound);

        controller = buildActivity(MainActivity_.class).setup();

        mainActivity = controller.get();
        controller.start();
    }

    @Test
    public void testStartButtonLaunchesGameActivityWithIntent() {
        Button startButton = (Button) mainActivity.findViewById(R.id.button_start_game);
        startButton.performClick();

        Intent expectedIntent = new Intent(mainActivity, GameActivity_.class);
        Assert.assertEquals(expectedIntent, Shadows.shadowOf(mainActivity).getNextStartedActivity());
    }

    @Test
    public void testHighscoresButtonLaunchesHighscoresActivityWithIntent() {
        Button highscoresButton = (Button) mainActivity.findViewById(R.id.button_highscores);
        highscoresButton.performClick();

        Intent expectedIntent = new Intent(mainActivity, HighscoreActivity_.class);
        Assert.assertEquals(expectedIntent, Shadows.shadowOf(mainActivity).getNextStartedActivity());
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
