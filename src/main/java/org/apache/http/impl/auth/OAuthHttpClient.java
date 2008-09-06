package org.apache.http.impl.auth;

import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.OAuthConsumer;
import org.apache.http.auth.OAuthToken;
import org.apache.http.impl.client.DefaultHttpClient;

public class OAuthHttpClient extends DefaultHttpClient {
    public OAuthHttpClient(final OAuthConsumer consumer, final OAuthToken token) {
    	// TODO support passing in a custom AuthScope
    	// TODO support passing in a signature method
    	// TODO support passing in an auth type (header vs. querystring)
    	getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new DefaultOAuthCredentials(consumer, token));
	}

	@Override
    protected AuthSchemeRegistry createAuthSchemeRegistry() {
    	AuthSchemeRegistry registry = super.createAuthSchemeRegistry();
    	registry.register(
    			OAuthScheme.SCHEME_NAME,
    			new OAuthSchemeFactory());
        return registry;
    }
}
