package org.apache.http.impl.auth;

import org.apache.http.auth.OAuthRequestToken;

public class DefaultOAuthRequestToken extends DefaultOAuthToken implements
		OAuthRequestToken {

	public DefaultOAuthRequestToken(final String key, final String secret) {
		super(key, secret);
	}

}
