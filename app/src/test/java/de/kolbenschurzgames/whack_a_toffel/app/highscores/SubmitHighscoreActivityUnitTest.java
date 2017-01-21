package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import de.kolbenschurzgames.whack_a_toffel.app.BuildConfig;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper_;

import org.junit.Assert;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.robolectric.Robolectric.buildActivity;

/**
 * Created by alfriedl on 20.02.15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "src/main/AndroidManifest.xml")
@PowerMockIgnore({"org.robolectric.*", "android.*", "org.mockito.*"})
@PrepareForTest(WebServiceHelper_.class)
public class SubmitHighscoreActivityUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private final int testScore = 100;
    private final Date testDate = new Date();
    private final String nickname = "nickname";

    @Mock
    private WebServiceHelper_ mockWebServiceHelper;

    @Captor
    private ArgumentCaptor<WebServiceCallback<Highscore>> callbackCaptor;

    @Captor
    private ArgumentCaptor<Highscore> highscoreCaptor;

    private SubmitHighscoreActivity_ submitHighscoreActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockStatic(WebServiceHelper_.class);

        when(WebServiceHelper_.getInstance_(any(Context.class))).thenReturn(mockWebServiceHelper);

        Intent testIntent = new Intent(ShadowApplication.getInstance().getApplicationContext(), SubmitHighscoreActivity_.class);
        testIntent.putExtra("score", testScore);
        testIntent.putExtra("endOfGame", testDate);

        submitHighscoreActivity = buildActivity(SubmitHighscoreActivity_.class).withIntent(testIntent).create().get();
    }

    private void submitHighscore(List<Highscore> submitResult) throws InterruptedException {
        Button submitButton = (Button) submitHighscoreActivity.findViewById(R.id.button_submit);
        EditText editText = (EditText) submitHighscoreActivity.findViewById(R.id.username_text_input);
        editText.setText(nickname);

        submitButton.performClick();

        // must wait here for the background task that uses the WebServiceHelper to complete
        Thread.sleep(100);

        verify(mockWebServiceHelper).submitHighscore(highscoreCaptor.capture(), callbackCaptor.capture());
        Highscore submittedHighscore = highscoreCaptor.getValue();
        Highscore expectedHighscore = new Highscore(nickname, testScore, testDate);
        Assert.assertEquals(expectedHighscore, submittedHighscore);

        callbackCaptor.getValue().onResultListReceived(submitResult);
    }

    @Test
    public void initSetsHighscoreText() {
        Assert.assertEquals(RuntimeEnvironment.application.getText(R.string.score) + ": " + testScore, submitHighscoreActivity.highscoresText.getText());
    }

    @Test
    public void submitButtonLaunchesHighscoreActivivityWithHighscoreHighlighted() throws InterruptedException {
        List<Highscore> submitResult = new ArrayList<>();
        Highscore responseHighscore = new Highscore(nickname, testScore, testDate, "1");
        submitResult.add(responseHighscore);

        submitHighscore(submitResult);

        assertHighscoreActivityLaunchedWithHighlightHighscoreExtra(responseHighscore);
    }

    @Test
    public void invalidServerResponseLaunchesHighscoreActivityWithoutHighlight() throws InterruptedException {
        List<Highscore> submitResult = new ArrayList<>();
        Highscore responseHighscore = new Highscore(nickname, testScore, testDate, "1");
        Highscore unexpectedHighscoreInResponse = new Highscore(nickname, 123, testDate, "2");
        submitResult.add(responseHighscore);
        submitResult.add(unexpectedHighscoreInResponse);

        submitHighscore(submitResult);

        assertHighscoreActivityLaunchedWithoutExtras();
    }

    @Test
    public void submitErrorLaunchesHighscoreActivity() throws InterruptedException {
        Button submitButton = (Button) submitHighscoreActivity.findViewById(R.id.button_submit);

        submitButton.performClick();

        // must wait here for the background task that uses the WebServiceHelper to complete
        Thread.sleep(100);

        verify(mockWebServiceHelper).submitHighscore(any(Highscore.class), callbackCaptor.capture());

        callbackCaptor.getValue().onError(new Error());
        assertHighscoreActivityLaunchedWithoutExtras();
    }

    private void assertHighscoreActivityLaunchedWithoutExtras() {
        Intent expectedIntent = new Intent(submitHighscoreActivity, HighscoreActivity_.class);
        Intent highscoreActivityIntent = Shadows.shadowOf(submitHighscoreActivity).getNextStartedActivity();
        Assert.assertEquals(expectedIntent, highscoreActivityIntent);
        Assert.assertNull(highscoreActivityIntent.getExtras());
    }

    private void assertHighscoreActivityLaunchedWithHighlightHighscoreExtra(Highscore toHighlight) {
        Intent expectedIntent = new Intent(submitHighscoreActivity, HighscoreActivity_.class);
        expectedIntent.putExtra("highlight", toHighlight.getId());
        Assert.assertEquals(expectedIntent, Shadows.shadowOf(submitHighscoreActivity).getNextStartedActivity());
    }
}
