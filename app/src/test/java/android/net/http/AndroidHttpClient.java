package android.net.http;

/**
 * Created by alfriedl on 21.01.17.
 *
 * Workaround for http://stackoverflow.com/questions/32759529/androidhttpclient-not-found-when-running-robolectric
 *
 * Issue is fixed in more recent releases of Robolectric but we can't upgrade at this point because of
 * https://github.com/robolectric/robolectric/issues/2208
 */
public class AndroidHttpClient {
}
