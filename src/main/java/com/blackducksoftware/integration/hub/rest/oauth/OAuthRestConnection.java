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
package com.blackducksoftware.integration.hub.rest.oauth;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpRequestInterceptor;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UriCombiner;
import com.blackducksoftware.integration.log.IntLogger;

public class OAuthRestConnection extends RestConnection {
    private final TokenManager tokenManager;
    private final OAuthAccess accessType;

    public OAuthRestConnection(final IntLogger logger, final URL hubBaseUrl, final int timeout, final TokenManager tokenManager, final OAuthAccess accessType, final ProxyInfo proxyInfo, final UriCombiner uriCombiner) {
        super(logger, hubBaseUrl, timeout, proxyInfo, uriCombiner);
        this.tokenManager = tokenManager;
        this.accessType = accessType;
    }

    @Override
    public void addBuilderAuthentication() throws IntegrationException {
        final HttpRequestInterceptor requestInterceptor = (httpRequest, context) -> {
            String credential;
            try {
                credential = tokenManager.createTokenCredential(tokenManager.getToken(accessType).accessToken);
            } catch (final IntegrationException e) {
                throw new IOException("Cannot refresh token", e);
            }
            httpRequest.addHeader(TokenManager.WWW_AUTH_RESP, credential);
        };
        getClientBuilder().addInterceptorLast(requestInterceptor);
    }

    @Override
    public void clientAuthenticate() throws IntegrationException {
        tokenManager.refreshToken(accessType);
    }

}
