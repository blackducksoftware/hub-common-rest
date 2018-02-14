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
package com.blackducksoftware.integration.hub.request;

import static com.blackducksoftware.integration.hub.RestConstants.QUERY_Q;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.util.Stringable;

public class GetRequestWrapper extends Stringable {
    private final Map<String, String> queryParameters = new HashMap<>();
    private String mimeType = ContentType.APPLICATION_JSON.getMimeType();
    private Charset bodyEncoding = Charsets.UTF_8;
    private final Map<String, String> additionalHeaders = new HashMap<>();
    private int limit = 100;
    private int offset = 0;

    public GetRequestWrapper setQ(final String q) {
        queryParameters.put(QUERY_Q, q);
        return this;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public GetRequestWrapper addQueryParameter(final String key, final String value) {
        queryParameters.put(key, value);
        return this;
    }

    public GetRequestWrapper addQueryParameters(final Map<String, String> parameters) {
        queryParameters.putAll(parameters);
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public GetRequestWrapper setOffset(final int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public GetRequestWrapper setLimit(final int limit) {
        this.limit = limit;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public GetRequestWrapper setMimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Charset getBodyEncoding() {
        return bodyEncoding;
    }

    public GetRequestWrapper setBodyEncoding(final Charset bodyEncoding) {
        this.bodyEncoding = bodyEncoding;
        return this;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public GetRequestWrapper addAdditionalHeader(final String key, final String value) {
        additionalHeaders.put(key, value);
        return this;
    }

    public GetRequestWrapper addAdditionalHeaders(final Map<String, String> parameters) {
        additionalHeaders.putAll(parameters);
        return this;
    }

    public Request createGetRequest(final String uri) {
        return new Request(uri, queryParameters, HttpMethod.GET, mimeType, bodyEncoding, additionalHeaders);
    }

    public PagedRequest createPagedRequest(final String uri) {
        return new PagedRequest(uri, queryParameters, HttpMethod.GET, mimeType, bodyEncoding, additionalHeaders, offset, limit);
    }
}
