/**
 * Hub Common Rest
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
package com.blackducksoftware.integration.hub

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.api.oauth.OAuthConfiguration
import com.blackducksoftware.integration.hub.api.oauth.Token
import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException
import com.blackducksoftware.integration.hub.rest.oauth.OAuthAccess
import com.blackducksoftware.integration.hub.rest.oauth.TokenManager
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class TokenManagerTest {
    public static final int CONNECTION_TIMEOUT = 213

    private final MockWebServer server = new MockWebServer();

    @Before public void setUp() throws Exception {
        server.start();
    }

    @After public void tearDown() throws Exception {
        server.shutdown();
    }

    private String getClientTokenJson(){
        getJsonFileContent('ClientToken.json')
    }

    private String getUserTokenJson(){
        getJsonFileContent('UserToken.json')
    }

    private String getJsonFileContent(String fileName){
        getClass().getResource("/$fileName").text
    }

    private TokenManager getTokenManager(){
        getTokenManager(null, null)
    }

    private TokenManager getTokenManager(String refreshToken){
        getTokenManager(null, refreshToken)
    }

    private TokenManager getTokenManager(MockResponse mockResponse){
        getTokenManager(mockResponse, null)
    }

    private TokenManager getTokenManager(MockResponse mockResponse, String refreshToken){
        final Dispatcher dispatcher = new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                        MockResponse response = null
                        if(null != mockResponse){
                            response = mockResponse
                        } else{
                            String body = request.getBody().readUtf8()
                            if(body.contains("grant_type=authorization_code")){
                                response = new MockResponse().setResponseCode(200).setBody(getUserTokenJson())
                            } else  if(body.contains("grant_type=client_credentials")){
                                response = new MockResponse().setResponseCode(200).setBody(getClientTokenJson())
                            } else  if(body.contains("grant_type=refresh_token")){
                                response = new MockResponse().setResponseCode(200).setBody(getUserTokenJson())
                            } else {
                                response = new MockResponse().setResponseCode(200)
                            }
                        }
                        response
                    }
                };
        server.setDispatcher(dispatcher);
        OAuthConfiguration oAuthConfig = new OAuthConfiguration()
        oAuthConfig.clientId = 'ClientId'
        oAuthConfig.authorizeUri = server.url("/authorize/").toString()
        oAuthConfig.tokenUri = server.url("/token/").toString()
        oAuthConfig.callbackUrl = server.url("/callback/").toString()
        oAuthConfig.refreshToken = refreshToken

        TokenManager tokenManager = new TokenManager(new PrintStreamIntLogger(System.out, LogLevel.TRACE), CONNECTION_TIMEOUT)
        tokenManager.setProxyInfo(ProxyInfo.NO_PROXY_INFO);
        tokenManager.setConfiguration(oAuthConfig)
        tokenManager
    }

    @Test
    public void testTokenManagerProperties() {
        TokenManager tokenManager = getTokenManager()
        assert null != tokenManager.getLogger()
        assert null != tokenManager.getTimeout()
        assert null != tokenManager.getConfiguration()
        assert !tokenManager.isAlwaysTrustServerCertificate()
        assert ProxyInfo.NO_PROXY_INFO == tokenManager.getProxyInfo()

        tokenManager.setAlwaysTrustServerCertificate(true)
        ProxyInfoBuilder builder = new ProxyInfoBuilder();
        builder.setHost("a.proxy.host")
        builder.setPort("12345")
        ProxyInfo proxyInfo = builder.build()
        tokenManager.setProxyInfo(proxyInfo)
        assert tokenManager.isAlwaysTrustServerCertificate()
        assert proxyInfo == tokenManager.getProxyInfo()
    }

    @Test
    public void testCreateTokenCredential(){
        TokenManager tokenManager = getTokenManager()
        String token = 'test'
        String output = tokenManager.createTokenCredential(token)
        assert null != output
        String expected = "Bearer $token"
        assert expected.equals(output)
    }

    @Test
    public void testExchangeForUserToken(){
        TokenManager tokenManager = getTokenManager()
        String authCode = 'AuthCode'
        Token token = tokenManager.exchangeForUserToken(authCode)
        assert null != token
        assert null != token.accessToken
        assert null != token.refreshToken
        assert null != token.tokenType
        assert null != token.expiresIn
        assert null != token.scope
        assert null != token.jti

        tokenManager = getTokenManager(new MockResponse().setResponseCode(404))
        try{
            tokenManager.exchangeForUserToken(authCode)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert 'Error refreshing client token'.equals(e.getMessage())
            IntegrationRestException restException = e.getCause()
            assert 404 == restException.httpStatusCode
        }
    }

    @Test
    public void testRefreshToken(){
        TokenManager tokenManager = getTokenManager()
        try{
            tokenManager.refreshToken(OAuthAccess.USER)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert "No token present to refresh".equals(e.getMessage())
        }
        String refreshToken = 'RefreshToken'
        tokenManager = getTokenManager(refreshToken)
        Token token = tokenManager.refreshToken(OAuthAccess.USER)
        assert null != token
        assert null != token
        assert null != token.accessToken
        assert null != token.refreshToken
        assert null != token.tokenType
        assert null != token.expiresIn
        assert null != token.scope
        assert null != token.jti

        assert null ==  tokenManager.clientToken

        token = tokenManager.refreshToken(OAuthAccess.CLIENT)
        Token storedClientToken = token
        assert null != token
        assert null != token
        assert null != token.accessToken
        assert null == token.refreshToken
        assert null != token.tokenType
        assert null != token.expiresIn
        assert null != token.scope
        assert null != token.jti

        assert null !=  tokenManager.clientToken

        token = tokenManager.refreshToken(OAuthAccess.CLIENT)
        assert null != token
        assert storedClientToken != token

        tokenManager = getTokenManager(new MockResponse().setResponseCode(404))
        try{
            tokenManager.refreshToken(OAuthAccess.CLIENT)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert 'Error refreshing client token'.equals(e.getMessage())
            IntegrationRestException restException = e.getCause()
            assert 404 == restException.httpStatusCode
        }
        tokenManager = getTokenManager(new MockResponse().setResponseCode(404), refreshToken)
        try{
            tokenManager.refreshToken(OAuthAccess.USER)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert 'Error refreshing user token'.equals(e.getMessage())
            IntegrationRestException restException = e.getCause()
            assert 404 == restException.httpStatusCode
        }
    }

    @Test
    public void testGetToken(){
        TokenManager tokenManager = getTokenManager()
        try{
            tokenManager.getToken(OAuthAccess.USER)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert "No token present to refresh".equals(e.getMessage())
        }
        String refreshToken = 'RefreshToken'
        tokenManager = getTokenManager(refreshToken)
        Token token = tokenManager.getToken(OAuthAccess.USER)
        assert null != token
        assert null != token
        assert null != token.accessToken
        assert null != token.refreshToken
        assert null != token.tokenType
        assert null != token.expiresIn
        assert null != token.scope
        assert null != token.jti

        assert null ==  tokenManager.clientToken

        token = tokenManager.getToken(OAuthAccess.CLIENT)
        Token storedClientToken = token
        assert null != token
        assert null != token
        assert null != token.accessToken
        assert null == token.refreshToken
        assert null != token.tokenType
        assert null != token.expiresIn
        assert null != token.scope
        assert null != token.jti

        assert null !=  tokenManager.clientToken

        token = tokenManager.getToken(OAuthAccess.CLIENT)
        assert null != token
        assert storedClientToken == token

        tokenManager = getTokenManager(new MockResponse().setResponseCode(404))
        try{
            tokenManager.getToken(OAuthAccess.CLIENT)
            fail('Should have thrown exception')
        } catch (IntegrationException e){
            assert 'Error refreshing client token'.equals(e.getMessage())
            IntegrationRestException restException = e.getCause()
            assert 404 == restException.httpStatusCode
        }
    }
}
