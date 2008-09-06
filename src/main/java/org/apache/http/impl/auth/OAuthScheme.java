package org.apache.http.impl.auth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.oauth.OAuth;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.OAuthCredentials;
import org.apache.http.client.InvalidRequestException;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class OAuthScheme implements AuthScheme {

	public static final String SCHEME_NAME = "OAuth";

	public Header authenticate(Credentials credentials, HttpRequest httpRequest)
			throws AuthenticationException {
		RequestWrapper request = null;
		if (httpRequest instanceof RequestWrapper) {
			request = (RequestWrapper) httpRequest;
		} else {
			throw new InvalidRequestException(
					"A RequestWrapper is required for OAuth signing: "
							+ httpRequest.getClass().getName());
		}

		OAuthCredentials oauthCredentials = null;
		try {
			oauthCredentials = (OAuthCredentials) credentials;
		} catch (ClassCastException e) {
			throw new InvalidCredentialsException(
					"Credentials cannot be used for OAuth authentication: "
							+ credentials.getClass().getName());
		}

		System.err.println("OAuthScheme#authenticate");
		System.out.println("Credentials: " + credentials.toString());

		List<NameValuePair> querystringParams = URLEncodedUtils.parse(request
				.getURI(), HTTP.UTF_8);
		System.out.println("Querystring params: " + querystringParams);

		// TODO get the protocol from somewhere
		String protocol = "http://";
		// TODO get the host from somewhere better
		// TODO normalize (remove 80 for http, 443 for https)
		String hostname = request.getHeaders("Host")[0].getValue();

		System.out.println("Method: " + request.getMethod());
		System.out.println("Request url: " + hostname
				+ request.getURI().getPath());
		System.out.println("Sorted querystring params: " + querystringParams);

		querystringParams.add(new BasicNameValuePair(OAuth.OAUTH_CONSUMER_KEY,
				oauthCredentials.getConsumer().getKey()));
		querystringParams.add(new BasicNameValuePair(OAuth.OAUTH_TOKEN,
				oauthCredentials.getToken().getKey()));
		querystringParams
				.add(new BasicNameValuePair(OAuth.OAUTH_NONCE, new Integer(
						new Random().nextInt(Integer.MAX_VALUE)).toString()));
		querystringParams.add(new BasicNameValuePair(OAuth.OAUTH_TIMESTAMP,
				new Long(System.currentTimeMillis() / 1000).toString()));

		querystringParams.add(new BasicNameValuePair(
				OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1));

		// TODO it would be really useful if NameValuePair were Comparable
		Collections.sort(querystringParams, new Comparator<NameValuePair>() {
			public int compare(NameValuePair o1, NameValuePair o2) {
				int retval = 0;

				if ((retval = o1.getName().compareTo(o2.getName())) == 0) {
					retval = o1.getValue().compareTo(o2.getValue());
				}

				return retval;
			}
		});

		// TODO merge querystringParams with POST params if relevant
		String sbs = OAuthUtils.getSignatureBaseString(request.getMethod(),
				OAuth.percentEncode(protocol + hostname
						+ request.getURI().getPath()), OAuth
						.percentEncode(URLEncodedUtils.format(
								querystringParams, HTTP.UTF_8)));
		System.out.println("Signature base string: " + sbs);

		String signatureKey = OAuth.percentEncode(oauthCredentials
				.getConsumer().getSecret())
				+ "&"
				+ OAuth.percentEncode(oauthCredentials.getToken().getSecret());

		// PLAINTEXT signing
		// querystringParams.add(new BasicNameValuePair(
		// OAuth.OAUTH_SIGNATURE_METHOD, "PLAINTEXT"));
		// querystringParams.add(new BasicNameValuePair(OAuth.OAUTH_SIGNATURE,
		// signatureKey));

		// HMAC-SHA1 signing
		String signature = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(signatureKey.getBytes(HTTP.UTF_8),
					"HmacSHA1"));
			byte[] text = sbs.getBytes(HTTP.UTF_8);
			byte[] signatureBytes = mac.doFinal(text);
			signature = new String(new Base64().encode(signatureBytes));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		querystringParams.add(new BasicNameValuePair(OAuth.OAUTH_SIGNATURE,
				signature));

		// mangle the url
		URI uri = request.getURI();
		request.setURI(URIUtils.resolve(uri, uri.getPath() + "?"
				+ URLEncodedUtils.format(querystringParams, HTTP.UTF_8)));

		// request.

		// "oauth_consumer_key"
		System.out.println("Querystring: " + request.getURI().getQuery());

		// CharArrayBuffer buffer = new CharArrayBuffer(32);
		// buffer.append(AUTH.WWW_AUTH_RESP);
		// buffer.append(": OAuth ");
		// String response = "foo";
		// buffer.append(response);
		//
		// return new BufferedHeader(buffer);
		return null;
	}

	public String getParameter(String name) {
		// String parameters not supported
		return null;
	}

	public String getRealm() {
		System.err.println("OAuthScheme#getRealm");
		// TODO do we support realms?
		return null;
	}

	public String getSchemeName() {
		return SCHEME_NAME;
	}

	public boolean isComplete() {
		System.err.println("OAuthScheme#isComplete");
		// TODO Let's pretend that everything is cool; in reality, might have to
		// exchange a request token for an access token or renew an access token
		return true;
	}

	public boolean isConnectionBased() {
		return false;
	}

	public void processChallenge(Header header)
			throws MalformedChallengeException {
		System.err.println("OAuthScheme#processChallenge");
		// TODO I suppose this is where we would convert request tokens to
		// access tokens or just refresh them
	}

}
