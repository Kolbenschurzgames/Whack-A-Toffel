package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.NetworkUtils;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper_;
import junit.framework.Assert;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.setupActivity;

/**
 * Created by alfriedl on 26.09.14.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({NetworkUtils.class, WebServiceHelper_.class})
public class HighscoreActivityUnitTest {

	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	private HighscoreActivity_ highscoreActivity;

	private TextView textView;
	private TableLayout highscoresTable;

	private WebServiceHelper_ mockWebServiceHelper;

	@Before
	public void setUp() {
		mockWebServiceHelper = PowerMockito.mock(WebServiceHelper_.class);
		mockStatic(NetworkUtils.class);
		mockStatic(WebServiceHelper_.class);
		when(WebServiceHelper_.getInstance_(any(Context.class))).thenReturn(mockWebServiceHelper);
	}
	
	@After
	public void tearDown() {
		reset(mockWebServiceHelper);
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
	public void testLoadingHighscoresInfoIfConnectionAvailable() {
		when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
		highscoreActivity = setupActivity(HighscoreActivity_.class);

		assertOnlyTextVisible();
		verify(mockWebServiceHelper, times(1)).getListOfHighscores(highscoreActivity);
		Assert.assertEquals(highscoreActivity.getString(R.string.loading_highscores), textView.getText());
	}

	@Test
	public void testErrorDisplayedOnLoadingHighscoresError() {
		when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
		highscoreActivity = setupActivity(HighscoreActivity_.class);

		ArgumentCaptor<WebServiceCallback> callbackCaptor = ArgumentCaptor.forClass(WebServiceCallback.class);
		verify(mockWebServiceHelper).getListOfHighscores(callbackCaptor.capture());
		callbackCaptor.getValue().onError(new Error("test error"));

		assertOnlyTextVisible();

		Assert.assertEquals(highscoreActivity.getString(R.string.load_highscores_error), textView.getText());
	}

	@Test
	public void testHighscoresDisplayed() {
		when(NetworkUtils.isConnectionAvailable(any(Context.class))).thenReturn(true);
		highscoreActivity = setupActivity(HighscoreActivity_.class);

		ArgumentCaptor<WebServiceCallback> callbackCaptor = ArgumentCaptor.forClass(WebServiceCallback.class);
		verify(mockWebServiceHelper).getListOfHighscores(callbackCaptor.capture());
		List<Highscore> highscores = buildHighscoreList();
		callbackCaptor.getValue().onResultListReceived(highscores);

		assertHighscoresVisible();
		assertHighscoresDisplayedCorrectly(highscores);
	}

	private void assertHighscoresDisplayedCorrectly(List<Highscore> highscores) {
		for (int i = 0; i < highscores.size(); i++) {
			TableRow currentRow = (TableRow) highscoresTable.getChildAt(i);
			Assert.assertNotNull(currentRow);
			TextView nameColumn = (TextView) currentRow.getChildAt(0);
			TextView scoreColumn = (TextView) currentRow.getChildAt(1);
			TextView dateColumn = (TextView) currentRow.getChildAt(2);
			if (i == 0) {
				Assert.assertEquals(highscoreActivity.getString(R.string.player), nameColumn.getText());
				Assert.assertEquals(highscoreActivity.getString(R.string.score), scoreColumn.getText());
				Assert.assertEquals(highscoreActivity.getString(R.string.date), dateColumn.getText());
			} else {
				Highscore score = highscores.get(i - 1);
				Assert.assertEquals(score.getName(), nameColumn.getText());
				Assert.assertEquals(Integer.toString(score.getScore()), scoreColumn.getText());
				Assert.assertEquals(highscoreActivity.buildLocalizedDateTimeString(score.getDate()), dateColumn.getText());
			}
		}
	}

	private void assertOnlyTextVisible() {
		assertViewsNotNull();
		Assert.assertEquals(View.VISIBLE, textView.getVisibility());
		Assert.assertEquals(View.INVISIBLE, highscoresTable.getVisibility());
	}

	private void assertHighscoresVisible() {
		assertViewsNotNull();
		Assert.assertEquals(View.INVISIBLE, textView.getVisibility());
		Assert.assertEquals(View.VISIBLE, highscoresTable.getVisibility());
	}

	private void assertViewsNotNull() {
		textView = (TextView) highscoreActivity.findViewById(R.id.highscores_text_view);
		highscoresTable = (TableLayout) highscoreActivity.findViewById(R.id.highscores_table);

		Assert.assertNotNull(textView);
		Assert.assertNotNull(highscoresTable);
	}

	private List<Highscore> buildHighscoreList() {
		List<Highscore> highscores = new ArrayList<Highscore>();
		Highscore score1 = new Highscore("score1", 1000, new Date());
		Highscore score2 = new Highscore("score2", 2000, new Date());
		highscores.add(score1);
		highscores.add(score2);
		return highscores;
	}
}