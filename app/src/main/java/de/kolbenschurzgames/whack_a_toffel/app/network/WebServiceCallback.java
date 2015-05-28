package de.kolbenschurzgames.whack_a_toffel.app.network;

import java.util.List;

/**
 * Created by alfriedl on 08.11.14.
 */
public interface WebServiceCallback<T> {

    void onResultListReceived(List<T> results);

    void onError(Error e);
}
