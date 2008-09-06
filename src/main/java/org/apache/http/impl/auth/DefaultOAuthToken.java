package org.apache.http.impl.auth;

import org.apache.http.auth.OAuthToken;

public abstract class DefaultOAuthToken extends AbstractOAuthToken implements OAuthToken {
	public DefaultOAuthToken(String key, String secret) {
		super(key, secret);
	}
}
