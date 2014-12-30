package de.kolbenschurzgames.whack_a_toffel.app.network;

/**
 * Created by alfriedl on 08.11.14.
 */
public class WebServiceError extends Error {

	public WebServiceError(String s) {
		super(s);
	}

	public WebServiceError(String s, Throwable e) {
		super(s, e);
	}
}
