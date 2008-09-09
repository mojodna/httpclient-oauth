package org.apache.http.impl.auth;

import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.OAuthAccessToken;
import org.apache.http.auth.OAuthConsumer;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

/**
 * OAuth test cases.
 * 
 * @author Seth Fitzsimmons
 */
public class TestOAuthScheme extends TestCase {
	private static final String CONSUMER_KEY = "consumer_key";
	private static final String CONSUMER_SECRET = "consumer_secret";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String ACCESS_TOKEN_SECRET = "access_token_secret";

	private AuthScheme scheme;
	private Credentials defaultCredentials;
	private HttpUriRequest defaultRequest;

	// ------------------------------------------------------------ Constructor
	public TestOAuthScheme(final String testName) throws IOException {
		super(testName);
	}

	// ------------------------------------------------------------------- Main
	public static void main(String args[]) {
		String[] testCaseName = { TestOAuthScheme.class.getName() };
		junit.textui.TestRunner.main(testCaseName);
	}

	// ------------------------------------------------------------------ Setup
	public void setUp() throws Exception {
		super.setUp();
		scheme = new OAuthScheme();
		OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
				CONSUMER_SECRET);
		OAuthAccessToken accessToken = new DefaultOAuthAccessToken(
				ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		defaultCredentials = new DefaultOAuthCredentials(consumer, accessToken);
		defaultRequest = new RequestWrapper(new BasicHttpRequest(
				HttpGet.METHOD_NAME, "http://example.org/"));
	}

	// ------------------------------------------------------- TestCase Methods

	public static Test suite() {
		TestSuite suite = new TestSuite(TestOAuthScheme.class);
		return suite;
	}

	public void testShouldRequireOAuthCredentials() throws Exception {
		try {
			scheme.authenticate(new UsernamePasswordCredentials("username",
					"password"), defaultRequest);
			fail("Should have thrown InvalidCredentialsException");
		} catch (InvalidCredentialsException e) {
			// expected
		}
	}

	public void testQuerystringAuthWithNoParametersShouldCreateAQuerystring()
			throws Exception {
		List<NameValuePair> querystringParams = URLEncodedUtils.parse(
				defaultRequest.getURI(), HTTP.UTF_8);
		assert (querystringParams.isEmpty());
		scheme.authenticate(defaultCredentials, defaultRequest);
		querystringParams = URLEncodedUtils.parse(defaultRequest.getURI(),
				HTTP.UTF_8);
		assertFalse(querystringParams.isEmpty());
	}

	public void testQuerystringAuthWithNoParametersShouldCreateAUniqueNonce()
			throws Exception {
		List<NameValuePair> querystringParams = URLEncodedUtils.parse(
				defaultRequest.getURI(), HTTP.UTF_8);
		assert (querystringParams.isEmpty());
		scheme.authenticate(defaultCredentials, defaultRequest);
		querystringParams = URLEncodedUtils.parse(defaultRequest.getURI(),
				HTTP.UTF_8);
		assertFalse(querystringParams.isEmpty());
	}

	// public void testQuerystringAuthWithGETParameters() throws Exception {
	// fail("Not implemented");
	// }
	//
	// public void testQuerystringAuthWithPOSTParameters() throws Exception {
	// fail("Not implemented");
	// }
	//
	// public void testQuerystringAuthWithGETAndPOSTParameters() throws
	// Exception {
	// fail("Not implemented");
	// }
	//
	// public void testHeaderAuthWithNoParameters() throws Exception {
	// fail("Not implemented");
	// }
	//
	// public void testHeaderAuthWithGETParameters() throws Exception {
	// fail("Not implemented");
	// }
	//
	// public void testHeaderAuthWithPOSTParameters() throws Exception {
	// fail("Not implemented");
	// }
	//
	// public void testHeaderAuthWithGETAndPOSTParameters() throws Exception {
	// fail("Not implemented");
	// }

	public void testBasicAuthenticationWithNoRealm() {
		String challenge = "Basic";
		Header header = new BasicHeader(AUTH.WWW_AUTH, challenge);
		try {
			AuthScheme authscheme = new BasicScheme();
			authscheme.processChallenge(header);
			fail("Should have thrown MalformedChallengeException");
		} catch (MalformedChallengeException e) {
			// expected
		}
	}

	public void testBasicAuthenticationWith88591Chars() throws Exception {
		int[] germanChars = { 0xE4, 0x2D, 0xF6, 0x2D, 0xFc };
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < germanChars.length; i++) {
			buffer.append((char) germanChars[i]);
		}

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"dh", buffer.toString());
		Header header = BasicScheme.authenticate(credentials, "ISO-8859-1",
				false);
		assertEquals("Basic ZGg65C32Lfw=", header.getValue());
	}

	public void testBasicAuthentication() throws Exception {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				"testuser", "testpass");

		Header challenge = new BasicHeader(AUTH.WWW_AUTH,
				"Basic realm=\"test\"");

		BasicScheme authscheme = new BasicScheme();
		authscheme.processChallenge(challenge);

		HttpRequest request = new BasicHttpRequest("GET", "/");
		Header authResponse = authscheme.authenticate(creds, request);

		String expected = "Basic "
				+ EncodingUtils.getAsciiString(Base64
						.encodeBase64(EncodingUtils
								.getAsciiBytes("testuser:testpass")));
		assertEquals(AUTH.WWW_AUTH_RESP, authResponse.getName());
		assertEquals(expected, authResponse.getValue());
		assertEquals("test", authscheme.getRealm());
		assertTrue(authscheme.isComplete());
		assertFalse(authscheme.isConnectionBased());
	}

	public void testBasicProxyAuthentication() throws Exception {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				"testuser", "testpass");

		Header challenge = new BasicHeader(AUTH.PROXY_AUTH,
				"Basic realm=\"test\"");

		BasicScheme authscheme = new BasicScheme();
		authscheme.processChallenge(challenge);

		HttpRequest request = new BasicHttpRequest("GET", "/");
		Header authResponse = authscheme.authenticate(creds, request);

		String expected = "Basic "
				+ EncodingUtils.getAsciiString(Base64
						.encodeBase64(EncodingUtils
								.getAsciiBytes("testuser:testpass")));
		assertEquals(AUTH.PROXY_AUTH_RESP, authResponse.getName());
		assertEquals(expected, authResponse.getValue());
		assertEquals("test", authscheme.getRealm());
		assertTrue(authscheme.isComplete());
		assertFalse(authscheme.isConnectionBased());
	}

}
