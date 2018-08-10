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
 * under the License.*/
package com.synopsys.integration.hub

import com.synopsys.integration.hub.rest.CredentialsRestConnectionBuilder
import com.synopsys.integration.log.LogLevel
import com.synopsys.integration.log.PrintStreamIntLogger
import com.synopsys.integration.rest.HttpMethod
import com.synopsys.integration.rest.connection.RestConnection
import com.synopsys.integration.rest.exception.IntegrationRestException
import com.synopsys.integration.rest.proxy.ProxyInfo
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.apache.http.client.methods.RequestBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class CredentialsRestConnectionTest {
    public static final int CONNECTION_TIMEOUT = 213

    private final MockWebServer server = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    private RestConnection getRestConnection(MockResponse response) {
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                response
            }
        };
        server.setDispatcher(dispatcher);
        CredentialsRestConnectionBuilder builder = new CredentialsRestConnectionBuilder();
        builder.logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);
        builder.baseUrl = server.url("/")
        builder.timeout = CONNECTION_TIMEOUT
        builder.username = 'TestUser'
        builder.password = 'Password'
        builder.applyProxyInfo(ProxyInfo.NO_PROXY_INFO);
        builder.build()
    }

    private MockResponse getSuccessResponse() {
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("Hello").setResponseCode(200);
    }

    private MockResponse getUnauthorizedResponse() {
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("Hello").setResponseCode(401);
    }

    private MockResponse getFailureResponse() {
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("Hello").setResponseCode(404);
    }

    @Test
    public void testHandleExecuteClientCallSuccessful() {
        RestConnection restConnection = getRestConnection(getSuccessResponse())
        RequestBuilder requestBuilder = restConnection.createRequestBuilder(HttpMethod.GET);
        restConnection.executeRequest(requestBuilder.build()).withCloseable { assert 200 == it.getStatusCode() }

        assert null != restConnection.getClientBuilder().cookieStore
        assert null != restConnection.getDefaultRequestConfigBuilder().cookieSpec
    }

    @Test
    public void testHandleExecuteClientCallUnauthorized() {
        RestConnection restConnection = getRestConnection(getUnauthorizedResponse())
        RequestBuilder requestBuilder = restConnection.createRequestBuilder(HttpMethod.GET);
        try {
            restConnection.executeRequest(requestBuilder.build())
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 401 == e.httpStatusCode
        }
    }


    @Test
    public void testHandleExecuteClientCallFail() {
        RestConnection restConnection = getRestConnection(getFailureResponse())
        RequestBuilder requestBuilder = restConnection.createRequestBuilder(HttpMethod.GET);
        try {
            restConnection.executeRequest(requestBuilder.build())
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 404 == e.httpStatusCode
        }
    }
}
