package de.kolbenschurzgames.whack_a_toffel.app.game;

import org.junit.Test;

/**
 * Created by henrypoetzl on 25.01.15.
 */
//@RunWith(RobolectricTestRunner.class)
//@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
//@PrepareForTest({Bitmap.class})
public class ToffelManagerUnitTest {

    @Test
    public void exampleTest() {
        assert(true);
    }
//    @Rule
//    public PowerMockRule powerMockRule = new PowerMockRule();
//
//    private final int SCREEN_WIDTH = 300;
//    private final int SCREEN_HEIGHT = 720;
//
//    private ToffelManager_ toffelManager;
//
//    @Before
//    public void setUp() {
//        mockStatic(Bitmap.class);
//
//        Context mockContext = mock(Context.class);
//
//        BitmapDrawable mockHoleToffel = mock(BitmapDrawable.class);
//        BitmapDrawable mockHoleEmpty = mock(BitmapDrawable.class);
//
//        Bitmap mockScaledHole = mock(Bitmap.class);
//        Bitmap mockScaledToffel = mock(Bitmap.class);
//        Bitmap mockHole = mock(Bitmap.class);
//        Bitmap mockToffel = mock(Bitmap.class);
//
//        Bitmap shadowBitmap = shadowOf_(Bitmap.class);
//
//        when(mockContext.getApplicationContext()).thenReturn(mockContext);
//        when(mockHoleToffel.getBitmap()).thenReturn(shadowBitmap);
//        when(mockHoleEmpty.getBitmap()).thenReturn(shadowBitmap);
//
//        when(Bitmap.createScaledBitmap(mockHole, anyInt(), anyInt(), false)).thenReturn(mockScaledHole);
//        when(Bitmap.createScaledBitmap(mockToffel, anyInt(), anyInt(), false)).thenReturn(mockScaledToffel);
//
//        toffelManager = ToffelManager_.getInstance_(mockContext);
//        toffelManager.toffel = mockToffel;
//        toffelManager.hole = mockHole;
//    }
//
//    @Test
//    public void testSetSizes() {
//        try {
//            toffelManager.setSizes(SCREEN_WIDTH, SCREEN_HEIGHT);
//        } catch (Exception e) {
//            Assert.fail();
//        } finally {
//            Assert.assertTrue(true);
//        }
//    }
//
//    @Test
//    public void testSetSizesThrowsErrorWhenWidthIsNegative() {
//        try {
//            toffelManager.setSizes(-SCREEN_WIDTH, SCREEN_HEIGHT);
//        } catch (Exception e) {
//            Assert.assertTrue(true);
//        }
//    }
//
//    @Test
//    public void testSetSizesThrowsErrorWhenHeightIsNegative() {
//        try {
//            toffelManager.setSizes(SCREEN_WIDTH, -SCREEN_HEIGHT);
//        } catch (Exception e) {
//            Assert.assertTrue(true);
//        }
//    }
}
