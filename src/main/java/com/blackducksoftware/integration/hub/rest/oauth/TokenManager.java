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

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.oauth.OAuthConfiguration;
import com.blackducksoftware.integration.hub.api.oauth.Token;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.hub.service.HubOAuthTokenService;
import com.blackducksoftware.integration.log.IntLogger;

public class TokenManager {
    public static final String WWW_AUTH_RESP = "Authorization";

    private final IntLogger logger;
    private final int timeout;
    private OAuthConfiguration configuration;
    private Token clientToken;
    private ProxyInfo proxyInfo;
    private boolean alwaysTrustServerCertificate;

    public TokenManager(final IntLogger logger, final int timeout) {
        this(logger, timeout, ProxyInfo.NO_PROXY_INFO, false);
    }

    public TokenManager(final IntLogger logger, final int timeout, final ProxyInfo proxyInfo, final boolean alwaysTrustServerCertificate) {
        this.logger = logger;
        this.timeout = timeout;
        this.proxyInfo = proxyInfo;
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;
    }

    public IntLogger getLogger() {
        return logger;
    }

    public int getTimeout() {
        return timeout;
    }

    public OAuthConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OAuthConfiguration configuration) {
        this.configuration = configuration;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(final ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public boolean isAlwaysTrustServerCertificate() {
        return alwaysTrustServerCertificate;
    }

    public void setAlwaysTrustServerCertificate(final boolean alwaysTrustServerCertificate) {
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;
    }

    public String createTokenCredential(final String token) {
        return String.format("Bearer %s", token);
    }

    public Token exchangeForUserToken(final String authorizationCode) throws IntegrationException {
        Token result = null;
        try {
            final RestConnection connection = createConnection();

            final HubOAuthTokenService tokenService = new HubOAuthTokenService(connection);
            result = tokenService.requestUserToken(configuration.clientId, authorizationCode, configuration.callbackUrl);
        } catch (final IntegrationException | MalformedURLException ex) {
            throw new IntegrationException("Error refreshing client token", ex);
        }

        return result;
    }

    public Token refreshToken(final OAuthAccess accessType) throws IntegrationException {
        Token result = null;
        if (OAuthAccess.USER.equals(accessType)) {
            result = refreshUserAccessToken();
        } else if (OAuthAccess.CLIENT.equals(accessType)) {
            result = refreshClientAccessToken();
        }

        return result;
    }

    public Token getToken(final OAuthAccess accessType) throws IntegrationException {
        Token result = null;
        if (OAuthAccess.USER.equals(accessType)) {
            result = refreshUserAccessToken();
        } else if (OAuthAccess.CLIENT.equals(accessType)) {
            if (clientToken == null) {
                refreshClientAccessToken();
            }
            result = clientToken;
        }

        return result;
    }

    private Token refreshUserAccessToken() throws IntegrationException {
        Token result = null;
        if (StringUtils.isNotBlank(configuration.refreshToken)) {
            try {
                final RestConnection connection = createConnection();

                final HubOAuthTokenService tokenService = new HubOAuthTokenService(connection);
                result = tokenService.refreshUserToken(configuration.clientId, configuration.refreshToken);
            } catch (final IntegrationException | MalformedURLException ex) {
                throw new IntegrationException("Error refreshing user token", ex);
            }
        } else {
            throw new IntegrationException("No token present to refresh");
        }

        return result;
    }

    private Token refreshClientAccessToken() throws IntegrationException {
        Token result = null;
        try {
            final RestConnection connection = createConnection();

            final HubOAuthTokenService tokenService = new HubOAuthTokenService(connection);
            result = tokenService.refreshClientToken(configuration.clientId);
            clientToken = result;
        } catch (final IntegrationException | MalformedURLException ex) {
            throw new IntegrationException("Error refreshing client token", ex);
        }
        return result;
    }

    private RestConnection createConnection() throws MalformedURLException {
        final UnauthenticatedRestConnectionBuilder connectionBuilder = new UnauthenticatedRestConnectionBuilder();
        connectionBuilder.setBaseUrl(configuration.tokenUri);
        connectionBuilder.setTimeout(timeout);
        connectionBuilder.setLogger(getLogger());
        connectionBuilder.setAlwaysTrustServerCertificate(alwaysTrustServerCertificate);
        connectionBuilder.applyProxyInfo(proxyInfo);

        final RestConnection connection = connectionBuilder.build();
        return connection;
    }

}
