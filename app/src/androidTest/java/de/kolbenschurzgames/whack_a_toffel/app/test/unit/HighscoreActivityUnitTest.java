package de.kolbenschurzgames.whack_a_toffel.app.test.unit;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper;
import junit.framework.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by alfriedl on 26.09.14.
 */
public class HighscoreActivityUnitTest extends ActivityUnitTestCase<HighscoreActivity> {

	private HighscoreActivity highscoreActivity;

	private TextView textView;
	private TableLayout highscoresTable;

	private ConnectivityManager mockNetworkManager;
	private NetworkInfo mockNetworkInfo;

	public HighscoreActivityUnitTest() {
		this(HighscoreActivity.class);
	}

	public HighscoreActivityUnitTest(Class<HighscoreActivity> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		System.setProperty(
				"dexmaker.dexcache",
				getInstrumentation().getTargetContext().getCacheDir().getPath());

		mockNetworkManager = mock(ConnectivityManager.class);
		mockNetworkInfo = mock(NetworkInfo.class);

		setActivityContext(buildTestContext());
	}

	@SmallTest
	public void testErrorMessageShownIfNoConnectionAvailable() {
		when(mockNetworkInfo.isConnected()).thenReturn(false);

		startHighscoresActivity();

		textView = (TextView) highscoreActivity.findViewById(R.id.highscores_text_view);
		highscoresTable = (TableLayout) highscoreActivity.findViewById(R.id.highscores_table);

		Assert.assertNotNull(textView);
		Assert.assertNotNull(highscoresTable);
		Assert.assertEquals(View.VISIBLE, textView.getVisibility());
		Assert.assertEquals(View.INVISIBLE, highscoresTable.getVisibility());

		Assert.assertEquals(highscoreActivity.getString(R.string.no_connection), textView.getText());
	}

	@SmallTest
	public void testLoadingHighscoresInfoIfConnectionAvailable() {
		startHighscoresActivity();

		textView = (TextView) highscoreActivity.findViewById(R.id.highscores_text_view);
		highscoresTable = (TableLayout) highscoreActivity.findViewById(R.id.highscores_table);

		Assert.assertNotNull(textView);
		Assert.assertNotNull(highscoresTable);
		Assert.assertEquals(View.VISIBLE, textView.getVisibility());
		Assert.assertEquals(View.INVISIBLE, highscoresTable.getVisibility());

		Assert.assertEquals(highscoreActivity.getString(R.string.loading_highscores), textView.getText());
	}

	private void startHighscoresActivity() {
		Intent highscoresIntent = new Intent(getInstrumentation().getTargetContext(), HighscoreActivity.class);
		startActivity(highscoresIntent, null, null);
		highscoreActivity = getActivity();
	}

	private Context buildTestContext() {
		when(mockNetworkManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
		when(mockNetworkInfo.isConnected()).thenReturn(true);

		Context testContext = new ContextWrapper(getInstrumentation().getTargetContext()) {
			@Override
			public Object getSystemService(String name) {
				if (name.equals(Context.CONNECTIVITY_SERVICE)) {
					return mockNetworkManager;
				} else {
					return super.getSystemService(name);
				}
			}
		};

		return testContext;
	}
}
