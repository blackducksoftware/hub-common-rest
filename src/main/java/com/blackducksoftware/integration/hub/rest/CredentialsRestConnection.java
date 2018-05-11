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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.rest.HttpMethod;
import com.blackducksoftware.integration.rest.RestConstants;
import com.blackducksoftware.integration.rest.connection.RestConnection;
import com.blackducksoftware.integration.rest.exception.IntegrationRestException;
import com.blackducksoftware.integration.rest.proxy.ProxyInfo;

public class CredentialsRestConnection extends RestConnection {
    private final String hubUsername;
    private final String hubPassword;

    public CredentialsRestConnection(final IntLogger logger, final URL baseUrl, final String hubUsername, final String hubPassword, final int timeout, final ProxyInfo proxyInfo) {
        super(logger, baseUrl, timeout, proxyInfo);
        this.hubUsername = hubUsername;
        this.hubPassword = hubPassword;
    }

    @Override
    public void addBuilderAuthentication() throws IntegrationRestException {
        if (StringUtils.isNotBlank(hubUsername) && StringUtils.isNotBlank(hubPassword)) {
            getClientBuilder().setDefaultCookieStore(new BasicCookieStore());
            getDefaultRequestConfigBuilder().setCookieSpec(CookieSpecs.DEFAULT);
        }
    }

    /**
     * Gets the cookie for the Authorized connection to the Hub server. Returns the response code from the connection.
     */
    @Override
    public void clientAuthenticate() throws IntegrationException {
        final URL securityUrl;
        try {
            securityUrl = new URL(baseUrl, "j_spring_security_check");
        } catch (MalformedURLException e) {
            throw new IntegrationException("Error constructing the login URL: " + e.getMessage(), e);
        }

        if (StringUtils.isNotBlank(hubUsername) && StringUtils.isNotBlank(hubPassword)) {
            final List<NameValuePair> bodyValues = new ArrayList<>();
            bodyValues.add(new BasicNameValuePair("j_username", hubUsername));
            bodyValues.add(new BasicNameValuePair("j_password", hubPassword));
            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(bodyValues, Charsets.UTF_8);

            final RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, null);
            requestBuilder.setCharset(Charsets.UTF_8);
            requestBuilder.setUri(securityUrl.toString());
            requestBuilder.setEntity(entity);
            final HttpUriRequest request = requestBuilder.build();
            logRequestHeaders(request);
            try (final CloseableHttpResponse response = getClient().execute(request)) {
                logResponseHeaders(response);
                final int statusCode = response.getStatusLine().getStatusCode();
                final String statusMessage = response.getStatusLine().getReasonPhrase();
                if (statusCode < RestConstants.OK_200 || statusCode >= RestConstants.MULT_CHOICE_300) {
                    throw new IntegrationRestException(statusCode, statusMessage, String.format("Connection Error: %s %s", statusCode, statusMessage));
                } else {
                    // get the CSRF token
                    final Header csrfToken = response.getFirstHeader(RestConstants.X_CSRF_TOKEN);
                    if (csrfToken != null) {
                        commonRequestHeaders.put(RestConstants.X_CSRF_TOKEN, csrfToken.getValue());
                    } else {
                        logger.error("No CSRF token found when authenticating");
                    }
                }
            } catch (final IOException e) {
                throw new IntegrationException(e.getMessage(), e);
            }
        }
    }

}
