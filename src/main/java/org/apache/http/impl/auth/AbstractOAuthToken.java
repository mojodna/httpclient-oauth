package org.apache.http.impl.auth;

public abstract class AbstractOAuthToken {
	private String key;
	private String secret;
	
	public AbstractOAuthToken(final String key, final String secret) {
		this.key = key;
		this.secret = secret;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
