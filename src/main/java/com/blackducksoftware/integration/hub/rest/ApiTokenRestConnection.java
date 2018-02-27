/**
 * hub-common-rest
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.rest;

import static com.blackducksoftware.integration.hub.RestConstants.X_CSRF_TOKEN;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.RestConstants;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException;
import com.blackducksoftware.integration.log.IntLogger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Connection to the Hub application which authenticates using the API token feature (added in Hub 4.4.0)
 */
public class ApiTokenRestConnection extends RestConnection {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String hubApiToken;

    public ApiTokenRestConnection(final IntLogger logger, final URL hubBaseUrl, final String hubApiToken, final int timeout, final ProxyInfo proxyInfo) {
        super(logger, hubBaseUrl, timeout, proxyInfo);
        this.hubApiToken = hubApiToken;
    }

    @Override
    public void addBuilderAuthentication() throws IntegrationRestException {
        // TODO romeara: This is a workaround because of HUB-13740, CSRF requires a session to work properly
        if (StringUtils.isNotBlank(hubApiToken)) {
            getClientBuilder().setDefaultCookieStore(new BasicCookieStore());
            getDefaultRequestConfigBuilder().setCookieSpec(CookieSpecs.DEFAULT);
        }
    }

    /**
     * Gets the cookie for the Authorized connection to the Hub server. Returns the response code from the connection.
     */
    @Override
    public void clientAuthenticate() throws IntegrationException {
        try {
            final URIBuilder uriBuilder = new URIBuilder(baseUrl.toURI());
            uriBuilder.setPath("api/tokens/authenticate");

            if (StringUtils.isNotBlank(hubApiToken)) {
                final RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, getRequestHeaders());
                requestBuilder.setCharset(Charsets.UTF_8);
                requestBuilder.setUri(uriBuilder.build());
                final HttpUriRequest request = requestBuilder.build();
                logRequestHeaders(request);
                try (final CloseableHttpResponse response = getClient().execute(request)) {
                    logResponseHeaders(response);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    final String statusMessage = response.getStatusLine().getReasonPhrase();
                    if (statusCode < RestConstants.OK_200 || statusCode >= RestConstants.MULT_CHOICE_300) {
                        throw new IntegrationRestException(statusCode, statusMessage, String.format("Connection Error: %s %s", statusCode, statusMessage));
                    } else {
                        commonRequestHeaders.put(AUTHORIZATION_HEADER, "Bearer " + readBearerToken(response));

                        // get the CSRF token
                        final Header csrfToken = response.getFirstHeader(X_CSRF_TOKEN);
                        if (csrfToken != null) {
                            commonRequestHeaders.put(X_CSRF_TOKEN, csrfToken.getValue());
                        } else {
                            logger.error("No CSRF token found when authenticating");
                        }
                    }
                } catch (final IOException e) {
                    throw new IntegrationException(e.getMessage(), e);
                }
            }
        } catch (final URISyntaxException e) {
            throw new IntegrationException(e.getMessage(), e);
        }

    }

    private Map<String, String> getRequestHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "token " + hubApiToken);

        return headers;
    }

    private String readBearerToken(final CloseableHttpResponse response) throws IOException {
        final JsonParser jsonParser = new JsonParser();
        String bodyToken = "";
        try (final InputStream inputStream = response.getEntity().getContent()) {
            bodyToken = IOUtils.toString(inputStream, Charsets.UTF_8);
        }
        final JsonObject bearerResponse = jsonParser.parse(bodyToken).getAsJsonObject();
        return bearerResponse.get("bearerToken").getAsString();
    }

}
