package org.apache.http.impl.auth;


public class OAuthUtils {
	public static String getSignatureBaseString(final String method, final String requestUrl, final String parameters) {
		return method + "&" + requestUrl + "&" + parameters;
	}
}
