package org.apache.http.impl.auth;

import org.apache.http.auth.OAuthConsumer;

public class DefaultOAuthConsumer extends AbstractOAuthToken implements
		OAuthConsumer {

	public DefaultOAuthConsumer(final String key, final String secret) {
		super(key, secret);
	}

}
