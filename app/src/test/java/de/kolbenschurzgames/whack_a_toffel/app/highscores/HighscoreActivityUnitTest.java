package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.BuildConfig;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.NetworkUtils;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper_;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.setupActivity;

/**
 * Created by alfriedl on 26.09.14.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
@PowerMockIgnore({"org.robolectric.*", "android.*", "org.mockito.*"})
@PrepareForTest({NetworkUtils.class, WebServiceHelper_.class})
public class HighscoreActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private HighscoreActivity_ highscoreActivity;

    private TextView textView;
    private ProgressBar progressBar;
    private TableLayout highscoresTable;

    @Mock
    private WebServiceHelper_ mockWebServiceHelper;

    @Captor
    private ArgumentCaptor<WebServiceCallback> callbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockStatic(NetworkUtils.class);
        mockStatic(WebServiceHelper_.class);
        when(WebServiceHelper_.getInstance_(any(Context.class))).thenReturn(mockWebServiceHelper);
    }

    @Test
    public void testErrorMessageShownIfNoConnectionAvailable() {
        when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(false);
        highscoreActivity = setupActivity(HighscoreActivity_.class);

        assertOnlyTextVisible();
        verifyZeroInteractions(mockWebServiceHelper);
        Assert.assertEquals(highscoreActivity.getString(R.string.no_connection), textView.getText());
    }

    @Test
    public void testProgressSpinnerIfConnectionAvailable() {
        when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
        highscoreActivity = setupActivity(HighscoreActivity_.class);

        assertOnlySpinnerVisible();
        verify(mockWebServiceHelper, times(1)).getListOfHighscores(highscoreActivity);
    }

    @Test
    public void testErrorDisplayedOnLoadingHighscoresError() {
        when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
        highscoreActivity = setupActivity(HighscoreActivity_.class);

        verify(mockWebServiceHelper).getListOfHighscores(callbackCaptor.capture());
        callbackCaptor.getValue().onError(new Error("test error"));

        assertOnlyTextVisible();

        Assert.assertEquals(highscoreActivity.getString(R.string.load_highscores_error), textView.getText());
    }

    @Test
    public void testHighscoresDisplayed() {
        when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
        highscoreActivity = setupActivity(HighscoreActivity_.class);

        verify(mockWebServiceHelper).getListOfHighscores(callbackCaptor.capture());
        List<Highscore> highscores = buildHighscoreList();
        callbackCaptor.getValue().onResultListReceived(highscores);

        assertHighscoresVisible();
        assertHighscoresDisplayedCorrectly(highscores);
    }

    @Test
    public void ownHighscoreHighlighted() throws InterruptedException {
        when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
        String highscoreId = "123af4";
        Intent testIntent = new Intent(ShadowApplication.getInstance().getApplicationContext(), HighscoreActivity_.class);
        testIntent.putExtra("highlight", highscoreId);

        highscoreActivity = buildActivity(HighscoreActivity_.class).withIntent(testIntent).create().get();
        // must wait here for the background task that uses the WebServiceHelper to complete
        Thread.sleep(100);

        verify(mockWebServiceHelper).getListOfHighscores(callbackCaptor.capture());
        List<Highscore> highscores = buildHighscoreList();
        highscores.add(new Highscore("name", 1, new Date(), highscoreId));
        callbackCaptor.getValue().onResultListReceived(highscores);

        assertHighscoresVisible();
        assertHighscoresDisplayedCorrectly(highscores);
        TableRow expectedHighlightedRow = (TableRow) highscoresTable.getChildAt(highscoresTable.getChildCount() - 1);
        Assert.assertNotNull(expectedHighlightedRow.getBackground());
        Assert.assertTrue(expectedHighlightedRow.getBackground() instanceof ColorDrawable);
    }

    private void assertHighscoresDisplayedCorrectly(List<Highscore> highscores) {
        TableRow headlineRow = (TableRow) highscoresTable.getChildAt(0);
        TextView headlineTextView = (TextView) headlineRow.getChildAt(0);
        Assert.assertEquals(highscoreActivity.getString(R.string.highscores), headlineTextView.getText());

        sortHighscoresDescendingByScore(highscores);

        for (int i = 1; i < highscores.size(); i++) {
            TableRow currentRow = (TableRow) highscoresTable.getChildAt(i);
            Assert.assertNotNull(currentRow);

            TextView positionColumn = (TextView) currentRow.getChildAt(0);
            TextView nameColumn = (TextView) currentRow.getChildAt(1);
            TextView scoreColumn = (TextView) currentRow.getChildAt(2);
            TextView dateColumn = (TextView) currentRow.getChildAt(3);

            if (i == 1) {
                Assert.assertEquals(highscoreActivity.getString(R.string.position), positionColumn.getText());
                Assert.assertEquals(highscoreActivity.getString(R.string.player), nameColumn.getText());
                Assert.assertEquals(highscoreActivity.getString(R.string.score), scoreColumn.getText());
                Assert.assertEquals(highscoreActivity.getString(R.string.date), dateColumn.getText());
            } else {
                Highscore score = highscores.get(i - 2);
                Assert.assertEquals(Integer.toString(i), positionColumn.getText());
                Assert.assertEquals(score.getName(), nameColumn.getText());
                Assert.assertEquals(Integer.toString(score.getScore()), scoreColumn.getText());
                Assert.assertEquals(highscoreActivity.buildLocalizedDateTimeString(score.getDate()), dateColumn.getText());
            }
        }
    }

    private void sortHighscoresDescendingByScore(List<Highscore> highscores) {
        Collections.sort(highscores, new Comparator<Highscore>() {
            @Override
            public int compare(Highscore hs1, Highscore hs2) {
                return hs2.getScore() - hs1.getScore();
            }
        });
    }

    private void assertOnlyTextVisible() {
        assertViewsNotNull();
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals(View.INVISIBLE, highscoresTable.getVisibility());
        Assert.assertEquals(View.GONE, progressBar.getVisibility());
    }

    private void assertOnlySpinnerVisible() {
        assertViewsNotNull();
        Assert.assertEquals(View.INVISIBLE, textView.getVisibility());
        Assert.assertEquals(View.INVISIBLE, highscoresTable.getVisibility());
        Assert.assertEquals(View.VISIBLE, progressBar.getVisibility());
    }

    private void assertHighscoresVisible() {
        assertViewsNotNull();
        Assert.assertEquals(View.INVISIBLE, textView.getVisibility());
        Assert.assertEquals(View.GONE, progressBar.getVisibility());
        Assert.assertEquals(View.VISIBLE, highscoresTable.getVisibility());
    }

    private void assertViewsNotNull() {
        textView = (TextView) highscoreActivity.findViewById(R.id.highscores_text_view);
        highscoresTable = (TableLayout) highscoreActivity.findViewById(R.id.highscores_table);
        progressBar = (ProgressBar) highscoreActivity.findViewById(R.id.highscores_progress);

        Assert.assertNotNull(textView);
        Assert.assertNotNull(highscoresTable);
        Assert.assertNotNull(progressBar);
    }

    private List<Highscore> buildHighscoreList() {
        List<Highscore> highscores = new ArrayList<Highscore>();
        Highscore score1 = new Highscore("score1", 1000, new Date());
        Highscore score2 = new Highscore("score2", 3000, new Date());
        Highscore score3 = new Highscore("score3", 2000, new Date());
        highscores.add(score1);
        highscores.add(score2);
        highscores.add(score3);
        return highscores;
    }
}
