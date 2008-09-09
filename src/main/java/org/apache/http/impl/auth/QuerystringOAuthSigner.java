package org.apache.http.impl.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.oauth.OAuthException;
import net.oauth.signature.OAuthSignatureMethod;

import org.apache.http.NameValuePair;
import org.apache.http.auth.OAuthConsumer;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

public class QuerystringOAuthSigner {
	private Class<? extends HttpRequestBase> method;
	private String requestUrl;
	private List<NameValuePair> parameters = Collections.emptyList();
	private Class<? extends OAuthSignatureMethod> signatureMethod;
	private String signature = null;
	private OAuthConsumer consumer;

	public QuerystringOAuthSigner(final OAuthConsumer consumer,
			final Class<? extends HttpRequestBase> method, final URI uri,
			final List<NameValuePair> parameters) {
		this(consumer, method, uri.toString(), parameters,
				OAuthSignatureMethod.class);
	}

	public QuerystringOAuthSigner(final OAuthConsumer consumer,
			final Class<? extends HttpRequestBase> method, final URI uri) {
		this(consumer, method, uri, null);
	}

	public QuerystringOAuthSigner(final OAuthConsumer consumer,
			final Class<? extends HttpRequestBase> method,
			final String requestUrl, final List<NameValuePair> parameters,
			final Class<? extends OAuthSignatureMethod> signatureMethod) {
		this.consumer = consumer;
		this.method = method;
		this.requestUrl = requestUrl;
		if (parameters != null) {
			this.parameters = parameters;
		}
		this.signatureMethod = signatureMethod;
	}

	public String getSignatureBaseString() {
		try {
			return method.newInstance().getMethod()
					+ "&"
					+ requestUrl
					+ "&"
					+ URLEncodedUtils.format(sortParameters(parameters),
							HTTP.UTF_8);
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not instantiate HTTP method.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not get the HTTP method.", e);
		}
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public void sign() {
		// short-circuit
		if (signature != null) {
			return;
		}

		try {
			signatureMethod.newInstance().sign(null);
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			throw new RuntimeException(
					"Could not instantiate the signature method.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Could not sign the signature base string.", e);
		}
	}

	public String getSignature() {
		sign();
		return signature;
	}

	public static List<NameValuePair> sortParameters(
			List<NameValuePair> parameters) {
		List<NameValuePair> sortedParameters = new ArrayList<NameValuePair>(
				parameters);

		// TODO it would be really useful if NameValuePair were Comparable
		Collections.sort(sortedParameters, new Comparator<NameValuePair>() {
			public int compare(NameValuePair o1, NameValuePair o2) {
				int retval = 0;

				if ((retval = o1.getName().compareTo(o2.getName())) == 0) {
					retval = o1.getValue().compareTo(o2.getValue());
				}

				return retval;
			}
		});

		return sortedParameters;
	}
}
