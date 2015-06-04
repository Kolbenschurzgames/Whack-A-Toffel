package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

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
import org.robolectric.shadows.ShadowCountDownTimer;

import java.util.Date;

import de.kolbenschurzgames.whack_a_toffel.app.highscores.SubmitHighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import de.kolbenschurzgames.whack_a_toffel.app.sound.GameSound_;
import de.kolbenschurzgames.whack_a_toffel.app.sound.SoundUtil_;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 25.01.15.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({ToffelManager_.class, SoundUtil_.class, GameSound_.class})
public class GameActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private GameActivity_ gameActivity;
    private ShadowCountDownTimer shadowCountDownTimer;

    private View mockView;
    private ToffelManager_ mockToffelManager;
    private SoundUtil_ mockSoundManager;

    private GameSound_ mockGameSound;


    @Before
    public void setUp() {
        mockStatic(ToffelManager_.class, SoundUtil_.class, GameSound_.class);

        mockView = mock(View.class);

        mockToffelManager = PowerMockito.mock(ToffelManager_.class);
        when(ToffelManager_.getInstance_(any(Context.class))).thenReturn(mockToffelManager);

        mockSoundManager = PowerMockito.mock(SoundUtil_.class);
        when(SoundUtil_.getInstance_(any(Context.class))).thenReturn(mockSoundManager);

        mockGameSound = PowerMockito.mock(GameSound_.class);
        when(GameSound_.getInstance_(any(Context.class))).thenReturn(mockGameSound);

        gameActivity = setupActivity(GameActivity_.class);
        shadowCountDownTimer = shadowOf(gameActivity.countDownTimer);

        doNothing().when(mockGameSound).stop();
        doNothing().when(mockGameSound).start(any(Context.class));
    }

    @After
    public void tearDown() {
        reset(mockToffelManager);
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
    public void testSubmitHighscoreActivityLaunchedAfterTimerExpired() {
        shadowCountDownTimer.invokeFinish();
        Intent startedActivityIntent = shadowOf(gameActivity).getNextStartedActivity();

        Assert.assertEquals(SubmitHighscoreActivity_.class.getName(), startedActivityIntent.getComponent().getClassName());
        Assert.assertEquals(0, startedActivityIntent.getExtras().get("score"));
        Assert.assertEquals(Date.class, startedActivityIntent.getExtras().get("endOfGame").getClass());
    }

    @Test
    public void testDownAndUpDifferentPosition() {
        float downEventX = 4.4f;
        float downEventY = 5.3f;
        float upEventX = 3.1f;
        float upEventY = 7.2f;

        MotionEvent actionDownEvent = mock(MotionEvent.class);
        when(actionDownEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
        when(actionDownEvent.getX()).thenReturn(downEventX);
        when(actionDownEvent.getY()).thenReturn(downEventY);

        MotionEvent actionUpEvent = mock(MotionEvent.class);
        when(actionUpEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
        when(actionUpEvent.getX()).thenReturn(upEventX);
        when(actionUpEvent.getY()).thenReturn(upEventY);

        when(mockToffelManager.getTapResult(downEventX, downEventY)).thenReturn(new ToffelTap(ToffelField.FIELD_BOTTOM_LEFT, true));
        when(mockToffelManager.getTapResult(upEventX, upEventY)).thenReturn(new ToffelTap(ToffelField.FIELD_BOTTOM_RIGHT, true));

        gameActivity.gameViewTouched(mockView, actionDownEvent);
        verify(mockToffelManager).getTapResult(downEventX, downEventY);

        gameActivity.gameViewTouched(mockView, actionUpEvent);
        verify(mockToffelManager).getTapResult(upEventX, upEventY);

        verify(mockToffelManager, never()).toffelTapped(any(ToffelField.class));
        Assert.assertEquals("0", gameActivity.gameView.getCurrentScore());
    }

    @Test
    public void testDownUnsuccessful() {
        float x = 4.4f;
        float y = 5.3f;
        ToffelField tappedField = ToffelField.FIELD_TOP_LEFT;

        MotionEvent actionDownEvent = mock(MotionEvent.class);
        when(actionDownEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
        when(actionDownEvent.getX()).thenReturn(x);
        when(actionDownEvent.getY()).thenReturn(y);

        MotionEvent actionUpEvent = mock(MotionEvent.class);
        when(actionUpEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
        when(actionUpEvent.getX()).thenReturn(x);
        when(actionUpEvent.getY()).thenReturn(y);

        when(mockToffelManager.getTapResult(x, y)).thenReturn(new ToffelTap(tappedField, false));

        gameActivity.gameViewTouched(mockView, actionDownEvent);
        gameActivity.gameViewTouched(mockView, actionUpEvent);

        verify(mockToffelManager, times(2)).getTapResult(x, y);
        verify(mockToffelManager, never()).toffelTapped(tappedField);
        Assert.assertEquals("0", gameActivity.gameView.getCurrentScore());
    }

    @Test
    public void testSuccessfulTap() {
        float x = 4.4f;
        float y = 5.3f;
        ToffelField tappedField = ToffelField.FIELD_TOP_LEFT;

        MotionEvent actionDownEvent = mock(MotionEvent.class);
        when(actionDownEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
        when(actionDownEvent.getX()).thenReturn(x);
        when(actionDownEvent.getY()).thenReturn(y);

        MotionEvent actionUpEvent = mock(MotionEvent.class);
        when(actionUpEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
        when(actionUpEvent.getX()).thenReturn(x);
        when(actionUpEvent.getY()).thenReturn(y);

        when(mockToffelManager.getTapResult(x, y)).thenReturn(new ToffelTap(tappedField, true));

        gameActivity.gameViewTouched(mockView, actionDownEvent);
        gameActivity.gameViewTouched(mockView, actionUpEvent);

        verify(mockToffelManager, times(2)).getTapResult(x, y);
        verify(mockToffelManager).toffelTapped(tappedField);
        Assert.assertEquals("1", gameActivity.gameView.getCurrentScore());
    }

    @Test
    public void testSoundAfterSuccessfulTap() {
        float x = 4.4f;
        float y = 5.3f;
        ToffelField tappedField = ToffelField.FIELD_TOP_LEFT;

        MotionEvent actionDownEvent = mock(MotionEvent.class);
        when(actionDownEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
        when(actionDownEvent.getX()).thenReturn(x);
        when(actionDownEvent.getY()).thenReturn(y);

        MotionEvent actionUpEvent = mock(MotionEvent.class);
        when(actionUpEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
        when(actionUpEvent.getX()).thenReturn(x);
        when(actionUpEvent.getY()).thenReturn(y);

        when(mockToffelManager.getTapResult(x, y)).thenReturn(new ToffelTap(tappedField, true));

        gameActivity.gameViewTouched(mockView, actionDownEvent);
        gameActivity.gameViewTouched(mockView, actionUpEvent);

        verify(mockSoundManager).playToffelTappedSound(any(Context.class));
    }

    @Test
    public void testSoundAfterMissedTap() {
        float x = 4.4f;
        float y = 5.3f;
        ToffelField tappedField = ToffelField.FIELD_TOP_LEFT;

        MotionEvent actionDownEvent = mock(MotionEvent.class);
        when(actionDownEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
        when(actionDownEvent.getX()).thenReturn(x);
        when(actionDownEvent.getY()).thenReturn(y);

        MotionEvent actionUpEvent = mock(MotionEvent.class);
        when(actionUpEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
        when(actionUpEvent.getX()).thenReturn(12345f);
        when(actionUpEvent.getY()).thenReturn(y);

        when(mockToffelManager.getTapResult(x, y)).thenReturn(new ToffelTap(tappedField, false));

        gameActivity.gameViewTouched(mockView, actionDownEvent);
        gameActivity.gameViewTouched(mockView, actionUpEvent);

        verify(mockSoundManager).playToffelMissedSound(any(Context.class));
    }

    @Test
    public void stopDrawingOnPause() {
        CountDownTimer mockCountDownTimer = mock(CountDownTimer.class);
        gameActivity.countDownTimer = mockCountDownTimer;
        GameView mockGameView = mock(GameView.class);
        gameActivity.gameView = mockGameView;

        gameActivity.onPause();

        verify(mockCountDownTimer).cancel();
        verify(mockGameView).stopDrawing();
    }

    @Test
    public void startDrawingOnResume() {
        GameView mockGameView = mock(GameView.class);
        gameActivity.gameView = mockGameView;

        gameActivity.onResume();

        shadowCountDownTimer = shadowOf(gameActivity.countDownTimer);
        Assert.assertTrue(shadowCountDownTimer.hasStarted());
        verify(mockGameView).startDrawing();
    }

    @Test
    public void soundStartsOnStartup() {
        verify(mockGameSound).start(any(Context.class));
    }

    @Test
    public void soundStopsOnPause() {
        gameActivity.onPause();
        verify(mockGameSound).stop();
    }
}
