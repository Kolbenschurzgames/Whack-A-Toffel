package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import de.kolbenschurzgames.whack_a_toffel.app.BuildConfig;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;

/**
 * Created by alfriedl on 30.12.14.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Highscore.class, RequestQueueSingleton.class})
public class WebServiceHelperUnitTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private Highscore highscoreMock;

    @Mock
    private RequestQueueSingleton mockRequestQueueSingleton;

    @Captor
    private ArgumentCaptor<JsonArrayRequest> jsonRequestArgCaptor;

    private WebServiceHelper_ webServiceHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockStatic(RequestQueueSingleton.class);
        mockStatic(Highscore.class);

        when(RequestQueueSingleton.getInstance(any(Context.class))).thenReturn(mockRequestQueueSingleton);

        Context mockContext = mock(Context.class);
        webServiceHelper = WebServiceHelper_.getInstance_(mockContext);
    }

    @Test
    public void testErrorResponse() {
        webServiceHelper.getListOfHighscores(new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                fail("Result callback should not have been triggered");
            }

            @Override
            public void onError(Error e) {
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof WebServiceError);
            }
        });

        ArgumentCaptor<JsonArrayRequest> argCaptor = ArgumentCaptor.forClass(JsonArrayRequest.class);
        verify(mockRequestQueueSingleton).addToRequestQueue(argCaptor.capture());

        JsonArrayRequest request = argCaptor.getValue();
        request.deliverError(new VolleyError("error"));
    }

    @Test
    public void testParseContentError() throws Exception {
        JSONArray responseArray = new JSONArray();
        when(Highscore.parseJsonArrayToList(responseArray)).thenThrow(new JSONException("error"));

        webServiceHelper.getListOfHighscores(new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                fail("Result callback should not have been triggered");
            }

            @Override
            public void onError(Error e) {
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof WebServiceError);
            }
        });

        verify(mockRequestQueueSingleton).addToRequestQueue(jsonRequestArgCaptor.capture());

        JsonArrayRequest request = jsonRequestArgCaptor.getValue();
        // Workaround because JsonRequest.deliverResponse is not public (unlike deliverError)
        Method deliverResponse = JsonRequest.class.getDeclaredMethod("deliverResponse", Object.class);
        deliverResponse.setAccessible(true);
        deliverResponse.invoke(request, responseArray);
    }

    @Test
    public void testDeliverResponse() throws Exception {
        JSONArray responseArray = new JSONArray();
        final List<Highscore> highscores = new ArrayList<Highscore>();
        highscores.add(new Highscore("name", 100, new Date()));
        highscores.add(new Highscore("name2", 200, new Date()));

        when(Highscore.parseJsonArrayToList(responseArray)).thenReturn(highscores);

        webServiceHelper.getListOfHighscores(new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                Assert.assertEquals(highscores, results);
            }

            @Override
            public void onError(Error e) {
                fail("Error callback should not have been triggered");
            }
        });

        verify(mockRequestQueueSingleton).addToRequestQueue(jsonRequestArgCaptor.capture());

        JsonArrayRequest request = jsonRequestArgCaptor.getValue();
        // Workaround because JsonRequest.deliverResponse is not public (unlike deliverError)
        Method deliverResponse = JsonRequest.class.getDeclaredMethod("deliverResponse", Object.class);
        deliverResponse.setAccessible(true);
        deliverResponse.invoke(request, responseArray);
    }

    @Test
    public void testSubmitErrorResponse() throws JSONException {
        webServiceHelper.submitHighscore(highscoreMock, new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                fail("Result callback should not have been triggered");
            }

            @Override
            public void onError(Error e) {
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof WebServiceError);
            }
        });

        verify(highscoreMock, times(1)).toJSON();

        verify(mockRequestQueueSingleton).addToRequestQueue(jsonRequestArgCaptor.capture());

        JsonArrayRequest request = jsonRequestArgCaptor.getValue();
        Assert.assertEquals(Request.Method.POST, request.getMethod());

        request.deliverError(new VolleyError("error"));
    }

    @Test
    public void testParseHighscoreError() throws Exception {
        when(highscoreMock.toJSON()).thenThrow(new JSONException("error"));

        webServiceHelper.submitHighscore(highscoreMock, new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                fail("Result callback should not have been triggered");
            }

            @Override
            public void onError(Error e) {
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof WebServiceError);
            }
        });

        verify(highscoreMock, times(1)).toJSON();
        verifyZeroInteractions(mockRequestQueueSingleton);
    }

    @Test
    public void testSubmitSuccessful() throws Exception {
        JSONArray responseArray = new JSONArray();
        final List<Highscore> highscores = new ArrayList<Highscore>();
        highscores.add(new Highscore("test", 1, new Date()));
        highscores.add(new Highscore("test2", 2, new Date()));

        when(Highscore.parseJsonArrayToList(responseArray)).thenReturn(highscores);

        webServiceHelper.submitHighscore(highscoreMock, new WebServiceCallback<Highscore>() {
            @Override
            public void onResultListReceived(List<Highscore> results) {
                Assert.assertEquals(highscores, results);
            }

            @Override
            public void onError(Error e) {
                fail("Error callback should not have been triggered");
            }
        });

        verify(highscoreMock, times(1)).toJSON();

        verify(mockRequestQueueSingleton).addToRequestQueue(jsonRequestArgCaptor.capture());

        JsonArrayRequest request = jsonRequestArgCaptor.getValue();
        Assert.assertEquals(Request.Method.POST, request.getMethod());

        // Workaround because JsonRequest.deliverResponse is not public (unlike deliverError)
        Method deliverResponse = JsonRequest.class.getDeclaredMethod("deliverResponse", Object.class);
        deliverResponse.setAccessible(true);
        deliverResponse.invoke(request, responseArray);
    }
}
