package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by alfriedl on 30.12.14.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Highscore.class, RequestQueueSingleton.class})
public class WebServiceHelperUnitTest {

	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	private WebServiceHelper_ webServiceHelper;

    private RequestQueueSingleton mockRequestQueueSingleton;

	@Before
	public void setUp() {
		mockStatic(RequestQueueSingleton.class);
        Context mockContext = mock(Context.class);
		mockRequestQueueSingleton = PowerMockito.mock(RequestQueueSingleton.class);
		when(RequestQueueSingleton.getInstance(any(Context.class))).thenReturn(mockRequestQueueSingleton);
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
		mockStatic(Highscore.class);
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

		ArgumentCaptor<JsonArrayRequest> argCaptor = ArgumentCaptor.forClass(JsonArrayRequest.class);
		verify(mockRequestQueueSingleton).addToRequestQueue(argCaptor.capture());

		JsonArrayRequest request = argCaptor.getValue();
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

		mockStatic(Highscore.class);
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

		ArgumentCaptor<JsonArrayRequest> argCaptor = ArgumentCaptor.forClass(JsonArrayRequest.class);
		verify(mockRequestQueueSingleton).addToRequestQueue(argCaptor.capture());

		JsonArrayRequest request = argCaptor.getValue();
		// Workaround because JsonRequest.deliverResponse is not public (unlike deliverError)
		Method deliverResponse = JsonRequest.class.getDeclaredMethod("deliverResponse", Object.class);
		deliverResponse.setAccessible(true);
		deliverResponse.invoke(request, responseArray);
	}
}
