package de.kolbenschurzgames.whack_a_toffel.app.network;

/**
 * Created by alfriedl on 08.11.14.
 */
class WebServiceError extends Error {

    WebServiceError(String s) {
        super(s);
    }

    WebServiceError(String s, Throwable e) {
        super(s, e);
    }
}
