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

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.util.Stringable;

public class RequestWrapper extends Stringable {
    private final Map<String, String> queryParameters = new HashMap<>();
    private String q;
    private final HttpMethod method;
    private String mimeType = ContentType.APPLICATION_JSON.getMimeType();
    private Charset bodyEncoding = Charsets.UTF_8;
    private final Map<String, String> additionalHeaders = new HashMap<>();
    private int limit = 100;
    private int offset = 0;

    private File bodyContentFile;
    private Map<String, String> bodyContentMap;
    private String bodyContent;
    private Object bodyContentObject;

    public RequestWrapper(final HttpMethod method) throws IntegrationException {
        this.method = method;
    }

    public RequestWrapper() throws IntegrationException {
        this.method = HttpMethod.GET;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getQ() {
        return q;
    }

    public RequestWrapper setQ(final String q) {
        this.q = q;
        return this;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public RequestWrapper addQueryParameter(final String key, final String value) {
        queryParameters.put(key, value);
        return this;
    }

    public RequestWrapper addQueryParameters(final Map<String, String> parameters) {
        queryParameters.putAll(parameters);
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public RequestWrapper setOffset(final int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public RequestWrapper setLimit(final int limit) {
        this.limit = limit;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public RequestWrapper setMimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Charset getBodyEncoding() {
        return bodyEncoding;
    }

    public RequestWrapper setBodyEncoding(final Charset bodyEncoding) {
        this.bodyEncoding = bodyEncoding;
        return this;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public RequestWrapper addAdditionalHeader(final String key, final String value) {
        additionalHeaders.put(key, value);
        return this;
    }

    public RequestWrapper addAdditionalHeaders(final Map<String, String> parameters) {
        additionalHeaders.putAll(parameters);
        return this;
    }

    public RequestWrapper setBodyContentFile(final File bodyContentFile) {
        this.bodyContentFile = bodyContentFile;
        return this;
    }

    public RequestWrapper setBodyContentMap(final Map<String, String> bodyContentMap) {
        this.bodyContentMap = bodyContentMap;
        return this;
    }

    public RequestWrapper setBodyContent(final String bodyContent) {
        this.bodyContent = bodyContent;
        return this;
    }

    public RequestWrapper setBodyContentObject(final Object bodyContentObject) {
        this.bodyContentObject = bodyContentObject;
        return this;
    }

    public Request createGetRequest(final String uri) {
        return new Request(uri, queryParameters, q, HttpMethod.GET, mimeType, bodyEncoding, additionalHeaders);
    }

    public Request createUpdateRequest(final String uri) {
        return new Request(uri, method, mimeType, bodyEncoding, additionalHeaders, bodyContentFile, bodyContentMap, bodyContent, bodyContentObject);
    }

    public PagedRequest createPagedRequest(final String uri) {
        return new PagedRequest(uri, queryParameters, q, method, mimeType, bodyEncoding, additionalHeaders, offset, limit);
    }
}
