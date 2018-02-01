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

import org.apache.commons.codec.Charsets
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.ContentType
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.request.PagedRequest
import com.blackducksoftware.integration.hub.request.Request
import com.blackducksoftware.integration.hub.rest.HttpMethod
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class RequestExecutionTest {
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
        if(null != response){
            final Dispatcher dispatcher = new Dispatcher() {
                        @Override
                        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                            response
                        }
                    };
            server.setDispatcher(dispatcher);
        }
        UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder()
        builder.logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE)
        builder.baseUrl = server.url("/").url()
        builder.timeout = CONNECTION_TIMEOUT
        builder.applyProxyInfo(ProxyInfo.NO_PROXY_INFO)
        builder.build()
    }

    @Test
    public void testCreateHttpRequest(){
        Request request = new Request(getRestConnection())
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String expectedUri = getRestConnection().baseUrl.toURI().toString()
        String actualUri = requestBuilder.uri.toString()
        assert expectedUri == actualUri
    }

    @Test
    public void testCreateHttpRequestChangingMethod(){
        Request request = new Request(getRestConnection())
        request.method = HttpMethod.PATCH

        assert HttpMethod.PATCH == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String expectedUri = getRestConnection().baseUrl.toURI().toString()
        String actualUri = requestBuilder.uri.toString()
        assert expectedUri == actualUri
    }

    @Test
    public void testCreateHttpRequestWithInvalidUrl(){
        Request request = new Request(getRestConnection())
        request.url = 'http :// a d v'
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null != request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        try {
            RequestBuilder requestBuilder = request.createHttpRequest()
            throw new Exception('Should have failed')
        } catch (URISyntaxException e) {
            assert e.getMessage().contains('Illegal character in scheme name')
        }
    }

    @Test
    public void testCreateHttpRequestWithUrl(){
        String expectedUri = 'http://test'
        Request request = new Request(getRestConnection())
        request.url = expectedUri
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null != request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String actualUri = requestBuilder.uri.toString()
        assert expectedUri == actualUri
    }


    @Test
    public void testCreateHttpRequestWithQ(){
        Request request = new Request(getRestConnection())
        request.q = 'test'
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null != request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()


        String actualUri = requestBuilder.uri.toString()
        assert actualUri.contains('q=test')
    }


    @Test
    public void testCreateHttpRequestWithQueries(){
        Request request = new Request(getRestConnection())
        request.addQueryParameter("queryName", "test")
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert !request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String actualUri = requestBuilder.uri.toString()
        assert actualUri.contains('queryName=test')
    }

    @Test
    public void testCreateHttpRequestWithQueryMap(){
        Request request = new Request(getRestConnection())
        def queries = [queryName:'test', second:'value']
        request.addQueryParameters(queries)
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert !request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String actualUri = requestBuilder.uri.toString()
        assert actualUri.contains('queryName=test')
        assert actualUri.contains('second=value')
    }

    @Test
    public void testCreateHttpRequestWithAdditionalHeader(){
        Request request = new Request(getRestConnection())
        request.addAdditionalHeader('test', 'thing')
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert !request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String expectedUri = getRestConnection().baseUrl.toURI().toString()
        String actualUri = requestBuilder.uri.toString()
        assert expectedUri == actualUri

        assert null != requestBuilder.getFirstHeader('test')
    }

    @Test
    public void testCreateHttpRequestWithAdditionalHeaders(){
        Request request = new Request(getRestConnection())
        def headers = [headerName:'test', second:'value']
        request.addAdditionalHeaders(headers)

        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert !request.additionalHeaders.isEmpty()

        RequestBuilder requestBuilder = request.createHttpRequest()

        String expectedUri = getRestConnection().baseUrl.toURI().toString()
        String actualUri = requestBuilder.uri.toString()
        assert expectedUri == actualUri

        assert null != requestBuilder.getFirstHeader('headerName')
        assert null != requestBuilder.getFirstHeader('second')
    }

    @Test
    public void testPagedRequest(){
        PagedRequest request = new PagedRequest(getRestConnection())
        def queries = [queryName:'test', second:'value']
        request.addQueryParameters(queries)

        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert !request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        assert 100 == request.limit
        assert 0 == request.offset

        RequestBuilder requestBuilder = request.createHttpRequest()

        String actualUri = requestBuilder.uri.toString()
        assert actualUri.contains('limit=100')
        assert actualUri.contains('queryName=test')
        assert actualUri.contains('offset=0')
        assert actualUri.contains('second=value')
    }

    @Test
    public void testExecuteGet(){
        Request request = new Request(getRestConnection())
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        request.execute().withCloseable{
            assert 200 == it.getStatusCode()
        }

        assert !request.additionalHeaders.isEmpty()
        assert ContentType.APPLICATION_JSON.getMimeType() == request.additionalHeaders.get(HttpHeaders.ACCEPT)
    }

    @Test
    public void testExecuteGetWithAccept(){
        Request request = new Request(getRestConnection())
        request.addAdditionalHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_ATOM_XML.getMimeType())

        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert !request.additionalHeaders.isEmpty()

        request.execute().withCloseable{
            assert 200 == it.getStatusCode()
        }
        assert ContentType.APPLICATION_ATOM_XML.getMimeType() == request.additionalHeaders.get(HttpHeaders.ACCEPT)
    }

    @Test
    public void testExecuteEmptyPostWithAccept(){
        Request request = new Request(getRestConnection())
        request.addAdditionalHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_ATOM_XML.getMimeType())
        request.method = HttpMethod.POST

        assert HttpMethod.POST == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert !request.additionalHeaders.isEmpty()

        request.execute().withCloseable{
            assert 200 == it.getStatusCode()
        }
        assert ContentType.APPLICATION_ATOM_XML.getMimeType() == request.additionalHeaders.get(HttpHeaders.ACCEPT)
    }

    @Test
    public void testExecutePostWithFile(){
        Request request = new Request(getRestConnection())
        request.method = HttpMethod.POST

        assert HttpMethod.POST == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        request.execute(new File('./README.md')).withCloseable{
            assert 200 == it.getStatusCode()
        }
    }

    @Test
    public void testExecutePostWithMap(){
        Request request = new Request(getRestConnection())
        request.method = HttpMethod.POST

        assert HttpMethod.POST == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        def bodyMap = [queryName:'test', second:'value']

        request.execute(bodyMap).withCloseable{
            assert 200 == it.getStatusCode()
        }
    }

    @Test
    public void testExecutePostWithString(){
        Request request = new Request(getRestConnection())
        request.method = HttpMethod.POST

        assert HttpMethod.POST == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.url
        assert null == request.q
        assert request.queryParameters.isEmpty()
        assert request.additionalHeaders.isEmpty()

        request.execute("TestBody").withCloseable{
            assert 200 == it.getStatusCode()
        }
    }
}
