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

import java.nio.charset.Charset

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.ContentType;
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder
import com.blackducksoftware.integration.hub.request.PagedRequest
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnectionBuilder
import com.blackducksoftware.integration.hub.rest.HttpMethod
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class RestConnectionTest {
    public static final int CONNECTION_TIMEOUT = 213

    private final MockWebServer server = new MockWebServer();

    @Before public void setUp() throws Exception {
        server.start();
    }

    @After public void tearDown() throws Exception {
        server.shutdown();
    }

    private RestConnection getRestConnection(){
        getRestConnection(new MockResponse().setResponseCode(200))
    }

    private RestConnection getRestConnection(MockResponse response){
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

    @Test
    public void testClientBuilding(){
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        int timeoutSeconds = 213
        int timeoutMilliSeconds = timeoutSeconds * 1000
        UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder()
        builder.logger = logger
        builder.baseUrl = server.url("/").url()
        builder.timeout = timeoutSeconds
        builder.applyProxyInfo(ProxyInfo.NO_PROXY_INFO)
        builder.alwaysTrustServerCertificate = true

        RestConnection restConnection = builder.build()
        def realClient = restConnection.client
        assert null == realClient
        restConnection.connect()
        realClient = restConnection.client
        assert timeoutMilliSeconds == realClient.defaultConfig.socketTimeout
        assert timeoutMilliSeconds == realClient.defaultConfig.connectionRequestTimeout
        assert timeoutMilliSeconds == realClient.defaultConfig.connectTimeout
        assert null == realClient.defaultConfig.proxy

        String proxyHost = "ProxyHost"
        int proxyPort = 3128
        String proxyIgnoredHosts = "IgnoredHost"
        String proxyUser = "testUser"
        String proxyPassword = "password"
        ProxyInfoBuilder proxyBuilder = new ProxyInfoBuilder()
        proxyBuilder.host = proxyHost
        proxyBuilder.port = proxyPort
        proxyBuilder.username = proxyUser
        proxyBuilder.password = proxyPassword
        proxyBuilder.ignoredProxyHosts = proxyIgnoredHosts
        ProxyInfo proxyInfo = proxyBuilder.build()
        builder = new UnauthenticatedRestConnectionBuilder()
        builder.logger = logger
        builder.baseUrl = server.url("/").url()
        builder.timeout = timeoutSeconds
        builder.applyProxyInfo(proxyInfo)
        restConnection = builder.build()

        restConnection.connect()
        realClient = restConnection.client
        assert null != realClient.defaultConfig.proxy

        proxyIgnoredHosts = ".*"
        proxyBuilder = new ProxyInfoBuilder()
        proxyBuilder.host = proxyHost
        proxyBuilder.port = proxyPort
        proxyBuilder.username = proxyUser
        proxyBuilder.password = proxyPassword
        proxyBuilder.ignoredProxyHosts = proxyIgnoredHosts
        proxyInfo = proxyBuilder.build()
        builder = new UnauthenticatedRestConnectionBuilder()
        builder.logger = logger
        builder.baseUrl = server.url("/").url()
        builder.timeout = timeoutSeconds
        builder.applyProxyInfo(proxyInfo)
        restConnection = builder.build()

        restConnection.connect()
        realClient = restConnection.client
        assert null == realClient.defaultConfig.proxy
    }

    @Test
    public void testToString(){
        RestConnection restConnection = getRestConnection()
        String s  = "RestConnection [baseUrl=${server.url("/").toString()}]"
        assert s.equals(restConnection.toString())
    }


    @Test
    public void testHandleExecuteClientCallSuccessful(){
        RestConnection restConnection = getRestConnection()
        restConnection.commonRequestHeaders.put("Common", "Header")
        RequestBuilder requestBuilder = restConnection.createRequestBuilder(HttpMethod.DELETE);
        assert null != requestBuilder.getHeaders("Common")

        restConnection.executeRequest(requestBuilder.build()).withCloseable{  assert 200 == it.getStatusCode() }
    }

    @Test
    public void testHandleExecuteClientCallFail(){
        RestConnection restConnection = getRestConnection()
        RequestBuilder requestBuilder = restConnection.createRequestBuilder(HttpMethod.GET);
        HttpUriRequest request = requestBuilder.build();
        restConnection.connect()

        restConnection = getRestConnection(new MockResponse().setResponseCode(404))
        try{
            restConnection.executeRequest(request)
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 404 == e.httpStatusCode
        }

        restConnection = getRestConnection(new MockResponse().setResponseCode(401))
        try{
            restConnection.executeRequest(request)
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 401 == e.httpStatusCode
        }
    }

    @Test
    public void testParsingDate(){
        String dateString = '2017-03-02T03:35:23.456Z'
        Date date = RestConnection.parseDateString(dateString)
        assert dateString.equals(RestConnection.formatDate(date))
    }

    @Test
    public void testCreateHttpRequestNoRequest() {
        RestConnection restConnection = new UnauthenticatedRestConnection(new PrintStreamIntLogger(System.out, LogLevel.TRACE), null, 300, ProxyInfo.NO_PROXY_INFO)
        try {
            restConnection.createHttpRequest(null)
            fail('Should have thrown exception')
        } catch (IllegalArgumentException e) {
            assert "Missing the Request" == e.getMessage()
        }
    }

    @Test
    public void testCreateHttpRequestNoURI() {
        RestConnection restConnection = new UnauthenticatedRestConnection(new PrintStreamIntLogger(System.out, LogLevel.TRACE), null, 300, ProxyInfo.NO_PROXY_INFO)
        PagedRequest pagedRequest = new PagedRequest(null)
        try {
            restConnection.createHttpRequest(pagedRequest)
            fail('Should have thrown exception')
        } catch (IllegalArgumentException e) {
            assert "Missing the URI" == e.getMessage()
        }
    }

    @Test
    public void testCreateHttpRequestNoMethod() {
        RestConnection restConnection = new UnauthenticatedRestConnection(new PrintStreamIntLogger(System.out, LogLevel.TRACE), null, 300, ProxyInfo.NO_PROXY_INFO)
        PagedRequest pagedRequest = new PagedRequest(null, null, null, null, null, null, null)
        try {
            restConnection.createHttpRequest(pagedRequest)
            fail('Should have thrown exception')
        } catch (IllegalArgumentException e) {
            assert "Missing the HttpMethod" == e.getMessage()
        }
    }

    @Test
    public void testCreateHttpRequest() {
        RestConnection restConnection = getRestConnection()

        String uri = restConnection.baseUrl.toURI().toString()
        Map<String, String> queryParametes = [test:"one",query:"two"]
        String q = 'q'
        HttpMethod method = HttpMethod.DELETE
        String mimeType = 'mime'
        Charset  bodyEncoding = Charsets.UTF_8
        Map<String, String> additionalHeaders = [header:"one",thing:"two"]

        PagedRequest pagedRequest = new PagedRequest(null)
        HttpRequestBase request = restConnection.createHttpRequest(pagedRequest)
        assert HttpMethod.GET.name() == request.method
        assert ContentType.APPLICATION_JSON.getMimeType() == request.getFirstHeader(HttpHeaders.ACCEPT).getValue()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=0')
        assert request.getURI().toString().contains('limit=100')


        pagedRequest = new PagedRequest(uri)
        request = restConnection.createHttpRequest(pagedRequest)
        assert HttpMethod.GET.name() == request.method
        assert ContentType.APPLICATION_JSON.getMimeType() == request.getFirstHeader(HttpHeaders.ACCEPT).getValue()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=0')
        assert request.getURI().toString().contains('limit=100')


        pagedRequest = new PagedRequest(null, null, null, method, null, null, null)
        request = restConnection.createHttpRequest(pagedRequest)
        assert method.name() == request.method
        assert 0 == request.getAllHeaders().size()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=0')
        assert request.getURI().toString().contains('limit=100')

        pagedRequest = new PagedRequest(uri, queryParametes, q, method, mimeType, bodyEncoding, additionalHeaders)
        request = restConnection.createHttpRequest(pagedRequest)
        assert method.name() == request.method
        assert 'one' == request.getFirstHeader('header').getValue()
        assert 'two' == request.getFirstHeader('thing').getValue()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=0')
        assert request.getURI().toString().contains('limit=100')

        pagedRequest = new PagedRequest(uri, queryParametes, q, method, mimeType, bodyEncoding, additionalHeaders, 5, 20)
        request = restConnection.createHttpRequest(pagedRequest)
        assert method.name() == request.method
        assert 'one' == request.getFirstHeader('header').getValue()
        assert 'two' == request.getFirstHeader('thing').getValue()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=20')
        assert request.getURI().toString().contains('limit=5')

        additionalHeaders.put(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.getMimeType())
        pagedRequest = new PagedRequest(uri, queryParametes, q, HttpMethod.GET, mimeType, bodyEncoding, additionalHeaders)
        request = restConnection.createHttpRequest(pagedRequest)
        assert HttpMethod.GET.name() == request.method
        assert ContentType.APPLICATION_XML.getMimeType() == request.getFirstHeader(HttpHeaders.ACCEPT).getValue()
        assert null != request.getURI()
        assert request.getURI().toString().contains(restConnection.baseUrl.toURI().toString())
        assert request.getURI().toString().contains('offset=0')
        assert request.getURI().toString().contains('limit=100')
    }


    //createResponse
}
