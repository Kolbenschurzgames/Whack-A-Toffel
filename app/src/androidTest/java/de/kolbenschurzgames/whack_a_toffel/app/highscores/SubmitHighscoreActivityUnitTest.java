package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper_;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by alfriedl on 20.02.15.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest(WebServiceHelper_.class)
public class SubmitHighscoreActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private final int testScore = 100;
    private final Date testDate = new Date();
    private final String nickname = "nickname";

    private SubmitHighscoreActivity_ submitHighscoreActivity;

    private WebServiceHelper_ mockWebServiceHelper;

    @Before
    public void setUp() {
        mockWebServiceHelper = PowerMockito.mock(WebServiceHelper_.class);
        mockStatic(WebServiceHelper_.class);
        when(WebServiceHelper_.getInstance_(any(Context.class))).thenReturn(mockWebServiceHelper);

        Intent testIntent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), SubmitHighscoreActivity_.class);
        testIntent.putExtra("score", testScore);
        testIntent.putExtra("endOfGame", testDate);

        submitHighscoreActivity = buildActivity(SubmitHighscoreActivity_.class).withIntent(testIntent).create().get();
    }

    @After
    public void tearDown() {
        reset(mockWebServiceHelper);
    }

    private void submitHighscore(List<Highscore> submitResult) {
        ArgumentCaptor<WebServiceCallback> callbackCaptor = ArgumentCaptor.forClass(WebServiceCallback.class);
        ArgumentCaptor<Highscore> highscoreCaptor = ArgumentCaptor.forClass(Highscore.class);

        Button submitButton = (Button) submitHighscoreActivity.findViewById(R.id.button_submit);
        EditText editText = (EditText) submitHighscoreActivity.findViewById(R.id.username_text_input);
        editText.setText(nickname);

        submitButton.performClick();

        verify(mockWebServiceHelper).submitHighscore(highscoreCaptor.capture(), callbackCaptor.capture());
        Highscore submittedHighscore = highscoreCaptor.getValue();
        Highscore expectedHighscore = new Highscore(nickname, testScore, testDate);
        Assert.assertEquals(expectedHighscore, submittedHighscore);

        callbackCaptor.getValue().onResultListReceived(submitResult);
    }

    @Test
    public void initSetsHighscoreText() {
        Assert.assertEquals(Robolectric.application.getText(R.string.score) + ": " + testScore, submitHighscoreActivity.highscoresText.getText());
    }

    @Test
    public void submitButtonLaunchesHighscoreActivivityWithHighscoreHighlighted() {
        List<Highscore> submitResult = new ArrayList<Highscore>();
        Highscore responseHighscore = new Highscore(nickname, testScore, testDate, "1");
        submitResult.add(responseHighscore);

        submitHighscore(submitResult);

        assertHighscoreActivityLaunchedWithHighlightHighscoreExtra(responseHighscore);
    }

    @Test
    public void invalidServerResponseLaunchesHighscoreActivityWithoutHighlight() {
        List<Highscore> submitResult = new ArrayList<Highscore>();
        Highscore responseHighscore = new Highscore(nickname, testScore, testDate, "1");
        Highscore unexpectedHighscoreInResponse = new Highscore(nickname, 123, testDate, "2");
        submitResult.add(responseHighscore);
        submitResult.add(unexpectedHighscoreInResponse);

        submitHighscore(submitResult);

        assertHighscoreActivityLaunchedWithoutExtras();
    }

    @Test
    public void submitErrorLaunchesHighscoreActivity() {
        ArgumentCaptor<WebServiceCallback> callbackCaptor = ArgumentCaptor.forClass(WebServiceCallback.class);

        Button submitButton = (Button) submitHighscoreActivity.findViewById(R.id.button_submit);

        submitButton.performClick();

        verify(mockWebServiceHelper).submitHighscore(any(Highscore.class), callbackCaptor.capture());

        callbackCaptor.getValue().onError(new Error());
        assertHighscoreActivityLaunchedWithoutExtras();
    }

    private void assertHighscoreActivityLaunchedWithoutExtras() {
        Intent expectedIntent = new Intent(submitHighscoreActivity, HighscoreActivity_.class);
        Intent highscoreActivityIntent = shadowOf(submitHighscoreActivity).getNextStartedActivity();
        Assert.assertEquals(expectedIntent, highscoreActivityIntent);
        Assert.assertNull(highscoreActivityIntent.getExtras());
    }

    private void assertHighscoreActivityLaunchedWithHighlightHighscoreExtra(Highscore toHighlight) {
        Intent expectedIntent = new Intent(submitHighscoreActivity, HighscoreActivity_.class);
        expectedIntent.putExtra("highlight", toHighlight.getId());
        Assert.assertEquals(expectedIntent, shadowOf(submitHighscoreActivity).getNextStartedActivity());
    }
}
