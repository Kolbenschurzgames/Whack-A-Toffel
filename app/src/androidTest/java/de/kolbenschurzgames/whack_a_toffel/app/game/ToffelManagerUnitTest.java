package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Toffel;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDrawable;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by henrypoetzl on 25.01.15.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(ToffelManager.class)
public class ToffelManagerUnitTest {

	private final int SCREEN_WIDTH = 300;
	private final int SCREEN_HEIGHT = 720;

	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	private ToffelManager_ toffelManager;

	private Toffel mockToffel;

	@Before
	public void setUp() throws Exception {
		toffelManager = ToffelManager_.getInstance_(Robolectric.getShadowApplication().getApplicationContext());

		mockToffel = mock(Toffel.class);
		PowerMockito.whenNew(Toffel.class).withAnyArguments().thenReturn(mockToffel);
	}

	@Test
	public void testResourceLoading() {
		ShadowDrawable shadowToffelDrawable = shadowOf(toffelManager.toffelResource);
		ShadowDrawable shadowHoleDrawable = shadowOf(toffelManager.emptyHoleResource);
		Assert.assertEquals(R.drawable.hole_toffel, shadowToffelDrawable.getCreatedFromResId());
		Assert.assertEquals(R.drawable.hole_empty, shadowHoleDrawable.getCreatedFromResId());
	}

	@Test
	public void testInitToffelHood() {
		try {
			toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);
		} catch (Exception e) {
			fail("No exception should have been thrown");
		} finally {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testUpdateToffels() {
		toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);

		Canvas mockCanvas = mock(Canvas.class);
		toffelManager.updateToffels(mockCanvas);

		int numOfToffelFields = 9;
		verify(mockToffel, times(numOfToffelFields)).updateToffel(mockCanvas);
	}

	@Test
	public void testToffelTapped() {
		toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);

		for (ToffelField field : ToffelField.values()) {
			toffelManager.toffelTapped(field);

			if (field.equals(ToffelField.NONE)) {
				verifyZeroInteractions(mockToffel);
			} else {
				verify(mockToffel).hideToffel();
				reset(mockToffel);
			}
		}
	}

	@Test
	public void testTapResultIsMissed() {
		toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);
		float x = 1.0f;
		float y = 2.0f;
		when(mockToffel.isTapped(x, y)).thenReturn(false);
		ToffelTap expectedTap = new ToffelTap(ToffelField.NONE, false);

		ToffelTap tap = toffelManager.getTapResult(x, y);
		Assert.assertEquals(expectedTap, tap);
	}

	@Test
	public void testTapResultIsTapped() throws Exception {
		Toffel tappedMockToffel = mock(Toffel.class);
		Point topLeftPoint = new Point(0, (SCREEN_HEIGHT - SCREEN_WIDTH) / 2);
		PowerMockito.whenNew(Toffel.class).withArguments(any(Bitmap.class), any(Bitmap.class), eq(topLeftPoint)).thenReturn(tappedMockToffel);
		toffelManager.initializeToffelHood(SCREEN_WIDTH, SCREEN_HEIGHT);
		float x = 1.0f;
		float y = 2.0f;
		when(tappedMockToffel.isTapped(x, y)).thenReturn(true);
		ToffelTap expectedTap = new ToffelTap(ToffelField.FIELD_TOP_LEFT, true);

		ToffelTap tap = toffelManager.getTapResult(x, y);
		Assert.assertEquals(expectedTap, tap);
	}
}
