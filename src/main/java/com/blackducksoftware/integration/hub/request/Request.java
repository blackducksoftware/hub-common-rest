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

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.util.Stringable;

public class Request extends Stringable {
    private final String uri;
    private final Map<String, String> queryParameters;
    private final String q;
    private final HttpMethod method;
    private final String mimeType;
    private final Charset bodyEncoding;
    private final Map<String, String> additionalHeaders;

    private File bodyContentFile;
    private Map<String, String> bodyContentMap;
    private String bodyContent;

    public Request(final String uri) {
        this.uri = uri;
        this.queryParameters = null;
        this.q = null;
        this.method = HttpMethod.GET;
        this.mimeType = ContentType.APPLICATION_JSON.getMimeType();
        this.bodyEncoding = Charsets.UTF_8;
        this.additionalHeaders = null;
    }

    public Request(final String uri, final Map<String, String> queryParameters, final String q, final HttpMethod method, final String mimeType, final Charset bodyEncoding,
            final Map<String, String> additionalHeaders) {
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.q = q;
        this.method = method;
        this.mimeType = mimeType;
        this.bodyEncoding = bodyEncoding;
        this.additionalHeaders = additionalHeaders;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public Map<String, String> getPopulatedQueryParameters() {
        final Map<String, String> populatedQueryParameters = new HashMap<>();
        if (StringUtils.isNotBlank(q)) {
            populatedQueryParameters.put(QUERY_Q, q);
        }
        if (this.queryParameters != null && !this.queryParameters.isEmpty()) {
            populatedQueryParameters.putAll(this.queryParameters);
        }
        return populatedQueryParameters;
    }

    public String getQ() {
        return q;
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

    public File getBodyContentFile() {
        return bodyContentFile;
    }

    public Map<String, String> getBodyContentMap() {
        return bodyContentMap;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContentFile(final File bodyContentFile) {
        this.bodyContentFile = bodyContentFile;
    }

    public void setBodyContentMap(final Map<String, String> bodyContentMap) {
        this.bodyContentMap = bodyContentMap;
    }

    public void setBodyContent(final String bodyContent) {
        this.bodyContent = bodyContent;
    }

}
