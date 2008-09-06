package org.apache.http.impl.auth;

import org.apache.http.auth.OAuthAccessToken;

public class DefaultOAuthAccessToken extends DefaultOAuthToken implements
		OAuthAccessToken {

	public DefaultOAuthAccessToken(String key, String secret) {
		super(key, secret);
	}
}
