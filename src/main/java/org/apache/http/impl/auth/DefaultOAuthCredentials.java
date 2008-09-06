package org.apache.http.impl.auth;

import java.security.Principal;

import org.apache.http.auth.OAuthConsumer;
import org.apache.http.auth.OAuthCredentials;
import org.apache.http.auth.OAuthToken;

public class DefaultOAuthCredentials implements OAuthCredentials {
	private OAuthConsumer consumer;
	private OAuthToken token;
	
	public DefaultOAuthCredentials(final OAuthConsumer consumer,
			final OAuthToken token) {
		this.consumer = consumer;
		this.token = token;
	}

	public String getPassword() {
		return null;
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public OAuthConsumer getConsumer() {
		return consumer;
	}

	public OAuthToken getToken() {
		return token;
	}
}
