package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import de.kolbenschurzgames.whack_a_toffel.app.BuildConfig;
import de.kolbenschurzgames.whack_a_toffel.app.model.Toffel;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelTap;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by henrypoetzl on 25.01.15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({ToffelImageProvider_.class, ToffelManager_.class})
public class ToffelManagerUnitTest {

    private final int NUM_TOFFEL_FIELDS = 9;
    private final int SCREEN_WIDTH = 300;
    private final int SCREEN_HEIGHT = 720;
    private final Point topLeftPoint = new Point(0, (SCREEN_HEIGHT - SCREEN_WIDTH) / 2);
    private final Point middleLeftPoint = new Point(0, (SCREEN_HEIGHT - SCREEN_WIDTH) / 2 + SCREEN_WIDTH / 3);

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private Toffel mockToffel;

    @Mock
    private ToffelImageProvider_ imageProviderMock;

    @Mock
    private Bitmap mockHoleBitmap;

    @Mock
    private Bitmap mockToffelImage;

    @Mock
    private Bitmap mockEvilToffelImage;

    @Mock
    private Canvas mockCanvas;
    private ToffelManager_ toffelManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(ToffelImageProvider_.class);

        PowerMockito.whenNew(Toffel.class).withAnyArguments().thenReturn(mockToffel);

        when(mockToffelImage.getWidth()).thenReturn(1);
        when(mockToffelImage.getHeight()).thenReturn(1);
        when(mockEvilToffelImage.getWidth()).thenReturn(1);
        when(mockEvilToffelImage.getHeight()).thenReturn(1);

        when(imageProviderMock.getScaledHole()).thenReturn(mockHoleBitmap);
        when(imageProviderMock.getImageForToffelType(any(ToffelType.class))).thenReturn(mockToffelImage);
        when(imageProviderMock.getImageForToffelType(eq(ToffelType.EVIL))).thenReturn(mockEvilToffelImage);
        when(ToffelImageProvider_.getInstance_(any(Context.class))).thenReturn(imageProviderMock);

        toffelManager = ToffelManager_.getInstance_(mock(Context.class));
        toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Test
    public void testUpdateToffelsEmptyField() {
        int xPos = 2;
        int yPos = 4;
        when(mockToffel.getXPosition()).thenReturn(xPos);
        when(mockToffel.getYPosition()).thenReturn(yPos);

        toffelManager.updateToffels(mockCanvas);

        verify(mockCanvas, times(NUM_TOFFEL_FIELDS)).drawBitmap(refEq(mockHoleBitmap), eq((float) xPos), eq((float) yPos), isNull(Paint.class));
        verify(mockToffel, times(NUM_TOFFEL_FIELDS)).randomizeState();
    }

    @Test
    public void testDrawToffels() throws Exception {
        final int evilToffelXPos = 3;
        final int evilToffelYPos = 2;
        final int regularToffelXPos = 4;
        final int regularToffelYPos = 1;

        Toffel evilToffel = mock(Toffel.class);
        when(evilToffel.isVisible()).thenReturn(true);
        when(evilToffel.getXPosition()).thenReturn(evilToffelXPos);
        when(evilToffel.getYPosition()).thenReturn(evilToffelYPos);
        when(evilToffel.getType()).thenReturn(ToffelType.EVIL);
        // test hide toffel after drawTicksCounter > Threshold
        when(evilToffel.getDrawTicksCounter()).thenReturn(10000);

        Toffel regularToffel = mock(Toffel.class);
        when(regularToffel.isVisible()).thenReturn(true);
        when(regularToffel.getXPosition()).thenReturn(regularToffelXPos);
        when(regularToffel.getYPosition()).thenReturn(regularToffelYPos);
        when(regularToffel.getType()).thenReturn(ToffelType.REGULAR);

        PowerMockito.whenNew(Toffel.class).withArguments(eq(middleLeftPoint)).thenReturn(evilToffel);
        PowerMockito.whenNew(Toffel.class).withArguments(eq(topLeftPoint)).thenReturn(regularToffel);
        toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);

        toffelManager.updateToffels(mockCanvas);

        verify(evilToffel, times(1)).incrementDrawTicksCounter();
        verify(regularToffel, times(1)).incrementDrawTicksCounter();
        verify(mockToffel, never()).incrementDrawTicksCounter();

        verify(mockCanvas, times(NUM_TOFFEL_FIELDS - 2)).drawBitmap(refEq(mockHoleBitmap), eq((float) 0), eq((float) 0), isNull(Paint.class));
        verify(mockCanvas, times(1)).drawBitmap(refEq(mockEvilToffelImage), eq((float) evilToffelXPos), eq((float) evilToffelYPos), isNull(Paint.class));
        verify(mockCanvas, times(1)).drawBitmap(refEq(mockToffelImage), eq((float) regularToffelXPos), eq((float) regularToffelYPos), isNull(Paint.class));

        verify(evilToffel, times(1)).hide();
        verify(evilToffel, times(1)).resetDrawTicksCounter();

        verify(regularToffel, never()).hide();
        verify(regularToffel, never()).resetDrawTicksCounter();
    }

    @Test
    public void testToffelTapped() {
        for (ToffelField field : ToffelField.values()) {
            toffelManager.toffelTapped(field);

            if (field.equals(ToffelField.NONE)) {
                verifyZeroInteractions(mockToffel);
            } else {
                verify(mockToffel).hide();
                reset(mockToffel);
            }
        }
    }

    @Test
    public void testTapResultIsMissed() throws Exception {
        float x = 1.0f;
        float y = 1.0f;
        Toffel hiddenEvilToffel = mock(Toffel.class);
        when(hiddenEvilToffel.isVisible()).thenReturn(false);
        when(hiddenEvilToffel.getXPosition()).thenReturn(1);
        when(hiddenEvilToffel.getYPosition()).thenReturn(1);
        when(hiddenEvilToffel.getType()).thenReturn(ToffelType.EVIL);

        PowerMockito.whenNew(Toffel.class).withArguments(eq(topLeftPoint)).thenReturn(hiddenEvilToffel);
        toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);

        ToffelTap expectedTap = new ToffelTap(ToffelField.FIELD_TOP_LEFT, false, ToffelType.EVIL);

        ToffelTap tap = toffelManager.getTapResult(x, y);
        Assert.assertEquals(expectedTap, tap);
    }

    @Test
    public void testTapResultIsTapped() throws Exception {
        float x = 1.0f;
        float y = 1.0f;
        Toffel tappedMockToffel = mock(Toffel.class);
        when(tappedMockToffel.isVisible()).thenReturn(true);
        when(tappedMockToffel.getXPosition()).thenReturn(1);
        when(tappedMockToffel.getYPosition()).thenReturn(1);
        when(tappedMockToffel.getType()).thenReturn(ToffelType.REGULAR);

        PowerMockito.whenNew(Toffel.class).withArguments(eq(middleLeftPoint)).thenReturn(tappedMockToffel);
        toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);

        ToffelTap expectedTap = new ToffelTap(ToffelField.FIELD_MIDDLE_LEFT, true, ToffelType.REGULAR);

        ToffelTap tap = toffelManager.getTapResult(x, y);
        Assert.assertEquals(expectedTap, tap);
    }
}
