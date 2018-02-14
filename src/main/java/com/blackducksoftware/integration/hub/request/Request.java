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

import static com.blackducksoftware.integration.hub.RestConstants.QUERY_LIMIT;
import static com.blackducksoftware.integration.hub.RestConstants.QUERY_OFFSET;
import static com.blackducksoftware.integration.hub.RestConstants.QUERY_Q;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.util.Stringable;

public class Request extends Stringable {
    private String uri;
    private Map<String, String> queryParameters;
    private HttpMethod method;
    private String mimeType;
    private Charset bodyEncoding;
    private Map<String, String> additionalHeaders;
    private File bodyContentFile;
    private Map<String, String> bodyContentMap;
    private String bodyContent;
    private Object bodyContentObject;

    public static Request createCommonGetRequest(final String uri) {
        final Request request = new Request();
        request.uri = uri;
        request.method = HttpMethod.GET;
        request.mimeType = ContentType.APPLICATION_JSON.getMimeType();
        request.bodyEncoding = Charsets.UTF_8;
        request.queryParameters = new HashMap<>();
        request.queryParameters.put(QUERY_OFFSET, String.valueOf(0));
        request.queryParameters.put(QUERY_LIMIT, String.valueOf(100));
        return request;
    }

    public static Request createCommonGetRequest(final String uri, final String q) {
        final Request request = createCommonGetRequest(uri);
        request.queryParameters = new HashMap<>();
        request.queryParameters.put(QUERY_Q, q);
        return request;
    }

    public static Request createCommonGetPagedRequest(final String uri, final String q, final int offset, final int limit) {
        final Request request = createCommonGetRequest(uri, q);
        request.queryParameters = new HashMap<>();
        request.queryParameters.put(QUERY_OFFSET, String.valueOf(offset));
        request.queryParameters.put(QUERY_LIMIT, String.valueOf(limit));
        return request;
    }

    private Request() {
        // only used by static factory methods
    }

    public Request(final String uri, final Map<String, String> queryParameters, final HttpMethod method, final String mimeType, final Charset bodyEncoding, final Map<String, String> additionalHeaders, final File bodyContentFile,
            final Map<String, String> bodyContentMap,
            final String bodyContent, final Object bodyContentObject) {
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.method = method;
        this.mimeType = mimeType;
        this.bodyEncoding = bodyEncoding;
        this.additionalHeaders = additionalHeaders;
        this.bodyContentFile = bodyContentFile;
        this.bodyContentMap = bodyContentMap;
        this.bodyContent = bodyContent;
        this.bodyContentObject = bodyContentObject;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getPopulatedQueryParameters() {
        final Map<String, String> populatedQueryParameters = new HashMap<>();
        if (getQueryParameters() != null && !getQueryParameters().isEmpty()) {
            populatedQueryParameters.putAll(getQueryParameters());
        }
        return populatedQueryParameters;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Charset getBodyEncoding() {
        return bodyEncoding;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public File getBodyContentFile() {
        return bodyContentFile;
    }

    public Map<String, String> getBodyContentMap() {
        return bodyContentMap;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public Object getBodyContentObject() {
        return bodyContentObject;
    }

}
