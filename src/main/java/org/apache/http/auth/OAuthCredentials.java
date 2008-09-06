package org.apache.http.auth;

public interface OAuthCredentials extends Credentials {
	OAuthConsumer getConsumer();

	OAuthToken getToken();
}
