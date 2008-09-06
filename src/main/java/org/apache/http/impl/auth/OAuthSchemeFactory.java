package org.apache.http.impl.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.params.HttpParams;

public class OAuthSchemeFactory implements AuthSchemeFactory {

    public AuthScheme newInstance(final HttpParams params) {
        return new OAuthScheme();
    }

}
