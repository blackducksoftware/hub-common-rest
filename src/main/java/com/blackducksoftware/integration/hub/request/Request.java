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
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.util.Stringable;

public class Request extends Stringable {
    public final RestConnection restConnection;
    public String url;
    public final List<String> urlSegments = new ArrayList<>();
    public final Map<String, String> queryParameters = new HashMap<>();
    public String q;
    public HttpMethod method = HttpMethod.GET;

    public String mimeType = ContentType.APPLICATION_JSON.getMimeType();
    public Charset bodyEncoding = Charsets.UTF_8;

    public final Map<String, String> additionalHeaders = new HashMap<>();

    public Request(final RestConnection restConnection) {
        this.restConnection = restConnection;
    }

    protected RequestBuilder createHttpRequest() throws IllegalArgumentException, URISyntaxException, IntegrationException {
        String baseUrl = null;
        if (url != null) {
            baseUrl = url;
        } else if (restConnection.baseUrl != null) {
            baseUrl = restConnection.baseUrl.toURI().toString();
        } else {
            throw new IntegrationException("Can not create this request without a URL");
        }
        final URIBuilder uriBuilder = new URIBuilder(baseUrl);
        if (urlSegments != null) {
            final String path = StringUtils.join(urlSegments, "/");
            uriBuilder.setPath(path);
        }
        populateQueryParameters();
        if (queryParameters != null) {
            for (final Entry<String, String> queryParameter : queryParameters.entrySet()) {
                uriBuilder.addParameter(queryParameter.getKey(), queryParameter.getValue());
            }
        }
        final RequestBuilder requestBuilder = restConnection.getRequestBuilder(method, additionalHeaders);
        requestBuilder.setUri(uriBuilder.build());
        return requestBuilder;
    }

    public Response execute() throws IntegrationException, IllegalArgumentException, URISyntaxException {
        if (HttpMethod.GET == method && !additionalHeaders.containsKey(HttpHeaders.ACCEPT)) {
            additionalHeaders.put(HttpHeaders.ACCEPT, mimeType);
        }
        final RequestBuilder requestBuilder = createHttpRequest();
        requestBuilder.setCharset(bodyEncoding);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    public Response execute(final File bodyContentFile) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final FileEntity entity = new FileEntity(bodyContentFile, ContentType.create(mimeType, bodyEncoding));
        return execute(entity);
    }

    public Response execute(final Map<String, String> bodyContentMap) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final List<NameValuePair> parameters = new ArrayList<>();
        if (bodyContentMap != null && !bodyContentMap.isEmpty()) {
            for (final Entry<String, String> entry : bodyContentMap.entrySet()) {
                final NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                parameters.add(nameValuePair);
            }
        }
        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, bodyEncoding);
        return execute(entity);
    }

    public Response execute(final String bodyContent) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final StringEntity entity = new StringEntity(bodyContent, ContentType.create(mimeType, bodyEncoding));
        return execute(entity);
    }

    private Response execute(final HttpEntity entity) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final RequestBuilder requestBuilder = createHttpRequest();
        requestBuilder.setCharset(bodyEncoding);
        requestBuilder.setEntity(entity);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    protected void populateQueryParameters() {
        if (StringUtils.isNotBlank(q)) {
            queryParameters.put(QUERY_Q, q);
        }
    }

    public void addUrlSegment(final String urlSegment) {
        urlSegments.add(urlSegment);
    }

    public void addUrlSegments(final List<String> urlSegment) {
        urlSegments.addAll(urlSegment);
    }

    public Request addQueryParameter(final String queryParameterName, final String queryParameterValue) {
        this.queryParameters.put(queryParameterName, queryParameterValue);
        return this;
    }

    public Request addQueryParameters(final Map<String, String> queryParameters) {
        this.queryParameters.putAll(queryParameters);
        return this;
    }

    public Request addAdditionalHeader(final String headerName, final String headerValue) {
        this.additionalHeaders.put(headerName, headerValue);
        return this;
    }

    public Request addAdditionalHeaders(final Map<String, String> additionalHeaders) {
        this.additionalHeaders.putAll(additionalHeaders);
        return this;
    }
}
