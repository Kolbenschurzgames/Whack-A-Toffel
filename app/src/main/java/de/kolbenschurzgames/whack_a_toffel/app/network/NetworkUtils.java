package de.kolbenschurzgames.whack_a_toffel.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by alfriedl on 19.09.14.
 */
public abstract class NetworkUtils {

	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
