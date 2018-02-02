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
import org.apache.http.entity.ContentType;
import org.junit.Test

import com.blackducksoftware.integration.hub.request.PagedRequest
import com.blackducksoftware.integration.hub.request.Request
import com.blackducksoftware.integration.hub.rest.HttpMethod

class RequestTest {

    @Test
    public void testRequest(){
        String uri = 'URI'
        Map<String, String> queryParametes = [test:"one",query:"two"]
        String q = 'q'
        HttpMethod method = HttpMethod.DELETE
        String mimeType = 'mime'
        Charset  bodyEncoding = Charsets.UTF_8
        Map<String, String> additionalHeaders = [header:"one",thing:"two"]

        Request request = new Request(null)
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert null == request.uri
        assert null == request.q
        assert null == request.queryParameters
        assert null == request.additionalHeaders
        assert request.getPopulatedQueryParameters().isEmpty()

        request = new Request(uri)
        assert HttpMethod.GET == request.method
        assert Charsets.UTF_8 == request.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == request.mimeType
        assert uri == request.uri
        assert null == request.q
        assert null == request.queryParameters
        assert null == request.additionalHeaders
        assert request.getPopulatedQueryParameters().isEmpty()

        request = new Request(null, null, null, null, null, null, null)
        assert null == request.method
        assert null == request.bodyEncoding
        assert null == request.mimeType
        assert null == request.uri
        assert null == request.q
        assert null == request.queryParameters
        assert null == request.additionalHeaders
        assert request.getPopulatedQueryParameters().isEmpty()

        request = new Request(uri, queryParametes, q, method, mimeType, bodyEncoding, additionalHeaders)
        assert method == request.method
        assert bodyEncoding == request.bodyEncoding
        assert mimeType == request.mimeType
        assert uri == request.uri
        assert q == request.q
        assert queryParametes == request.queryParameters
        assert additionalHeaders == request.additionalHeaders
        assert request.getPopulatedQueryParameters().size() == 3
    }

    @Test
    public void testPagedRequest(){
        String uri = 'URI'
        Map<String, String> queryParametes = [test:"one",query:"two"]
        String q = 'q'
        HttpMethod method = HttpMethod.DELETE
        String mimeType = 'mime'
        Charset  bodyEncoding = Charsets.UTF_8
        Map<String, String> additionalHeaders = [header:"one",thing:"two"]

        PagedRequest pagedRequest = new PagedRequest(null)
        assert HttpMethod.GET == pagedRequest.method
        assert Charsets.UTF_8 == pagedRequest.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == pagedRequest.mimeType
        assert null == pagedRequest.uri
        assert null == pagedRequest.q
        assert null == pagedRequest.queryParameters
        assert null == pagedRequest.additionalHeaders
        assert 100 == pagedRequest.limit
        assert 0 == pagedRequest.offset
        assert pagedRequest.getPopulatedQueryParameters().size() == 2

        pagedRequest = new PagedRequest(uri)
        assert HttpMethod.GET == pagedRequest.method
        assert Charsets.UTF_8 == pagedRequest.bodyEncoding
        assert ContentType.APPLICATION_JSON.getMimeType() == pagedRequest.mimeType
        assert uri == pagedRequest.uri
        assert null == pagedRequest.q
        assert null == pagedRequest.queryParameters
        assert null == pagedRequest.additionalHeaders
        assert 100 == pagedRequest.limit
        assert 0 == pagedRequest.offset
        assert pagedRequest.getPopulatedQueryParameters().size() == 2


        pagedRequest = new PagedRequest(null, null, null, null, null, null, null)
        assert null == pagedRequest.method
        assert null == pagedRequest.bodyEncoding
        assert null == pagedRequest.mimeType
        assert null == pagedRequest.uri
        assert null == pagedRequest.q
        assert null == pagedRequest.queryParameters
        assert null == pagedRequest.additionalHeaders
        assert 100 == pagedRequest.limit
        assert 0 == pagedRequest.offset
        assert pagedRequest.getPopulatedQueryParameters().size() == 2

        pagedRequest = new PagedRequest(uri, queryParametes, q, method, mimeType, bodyEncoding, additionalHeaders)
        assert method == pagedRequest.method
        assert bodyEncoding == pagedRequest.bodyEncoding
        assert mimeType == pagedRequest.mimeType
        assert uri == pagedRequest.uri
        assert q == pagedRequest.q
        assert queryParametes == pagedRequest.queryParameters
        assert additionalHeaders == pagedRequest.additionalHeaders
        assert 100 == pagedRequest.limit
        assert 0 == pagedRequest.offset
        assert pagedRequest.getPopulatedQueryParameters().size() == 5

        pagedRequest = new PagedRequest(uri, queryParametes, q, method, mimeType, bodyEncoding, additionalHeaders, 5, 20)
        assert method == pagedRequest.method
        assert bodyEncoding == pagedRequest.bodyEncoding
        assert mimeType == pagedRequest.mimeType
        assert uri == pagedRequest.uri
        assert q == pagedRequest.q
        assert queryParametes == pagedRequest.queryParameters
        assert additionalHeaders == pagedRequest.additionalHeaders
        assert 5 == pagedRequest.limit
        assert 20 == pagedRequest.offset
        assert pagedRequest.getPopulatedQueryParameters().size() == 5
    }
}
