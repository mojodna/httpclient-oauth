package org.apache.http.examples.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.OAuthConsumer;
import org.apache.http.auth.OAuthToken;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.DefaultOAuthAccessToken;
import org.apache.http.impl.auth.DefaultOAuthConsumer;
import org.apache.http.impl.auth.OAuthHttpClient;
import org.apache.http.impl.auth.OAuthScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * A simple example that uses HttpClient to execute an HTTP request against a
 * target site that uses OAuth.
 */
public class ClientOAuthAuthentication {

	public static void main(String[] args) throws Exception {
		OAuthConsumer consumer = new DefaultOAuthConsumer("key", "secret");
		OAuthToken token = new DefaultOAuthAccessToken("accesskey", "accesssecret");
		DefaultHttpClient httpclient = new OAuthHttpClient(consumer, token);

		
		// TODO put all of this stuff into OAuthHttpClient
		BasicHttpContext localcontext = new BasicHttpContext();

		// Generate OAuth scheme object and stick it in the local
		// execution context
		OAuthScheme oauth = new OAuthScheme();
		localcontext.setAttribute("preemptive-auth", oauth);

		// Add as the first request interceptor
		httpclient.addRequestInterceptor(new PreemptiveAuth(), 0);
		httpclient.addRequestInterceptor(new DumpRequest());
		// TODO end of stuff to put into OAuthHttpClient

		HttpGet httpget = new HttpGet(
				"http://oauth.term.ie/oauth/example/echo_api.php?foo=bar");

		HttpResponse response = httpclient.execute(httpget,
				localcontext);
		HttpEntity entity = response.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(response.getStatusLine());
		if (entity != null) {
			System.out.println("Response content length: "
					+ entity.getContentLength());
			System.out.println("Chunked?: " + entity.isChunked());
			System.out.println("Body:");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			entity.consumeContent();
		}
	}

	static class PreemptiveAuth implements HttpRequestInterceptor {

		public void process(final HttpRequest request, final HttpContext context)
				throws HttpException, IOException {

			AuthState authState = (AuthState) context
					.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it
			// preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context
						.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider
							.getCredentials(new AuthScope(targetHost
									.getHostName(), targetHost.getPort()));
					if (creds == null) {
						throw new HttpException(
								"No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

		}
	}

	static class DumpRequest implements HttpRequestInterceptor {
		public void process(final HttpRequest request, final HttpContext context)
				throws HttpException, IOException {

			System.out.println("executing request: " + request.getRequestLine());
			if (request.getAllHeaders().length > 0) {
				System.out.println("with headers:");
				for (Header header : request.getAllHeaders()) {
					System.out.println(header.getName() + ": " + header.getValue());
				}
			}
			
			System.out.println("with querystring: " + ((HttpUriRequest) request).getURI().getQuery());

		}
	}
}
