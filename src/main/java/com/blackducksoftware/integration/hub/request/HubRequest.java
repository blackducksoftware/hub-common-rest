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
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
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

public class HubRequest {
    public final RestConnection restConnection;
    public String url;
    public final List<String> urlSegments = new ArrayList<>();
    public final Map<String, String> queryParameters = new HashMap<>();
    public String q;

    public HubRequest(final RestConnection restConnection) {
        this.restConnection = restConnection;
    }

    private RequestBuilder createHttpRequest(final HttpMethod method, final Map<String, String> additionalHeaders) throws IllegalArgumentException, URISyntaxException, IntegrationException {
        String baseUrl = null;
        if (url != null) {
            baseUrl = url;
        } else if (restConnection.hubBaseUrl != null) {
            baseUrl = restConnection.hubBaseUrl.toURI().toString();
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

    public HttpResponse executeGet() throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeGet(ContentType.APPLICATION_JSON.getMimeType());
    }

    public HttpResponse executeGet(final String mimeType) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(HttpHeaders.ACCEPT, mimeType);
        return executeGet(additionalHeaders);
    }

    public HttpResponse executeGet(final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final RequestBuilder requestBuilder = createHttpRequest(HttpMethod.GET, additionalHeaders);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    public HttpResponse executeEncodedFormPost(final Map<String, String> contentMap) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedFormPost(contentMap, Charsets.UTF_8);
    }

    public HttpResponse executeEncodedFormPost(final Map<String, String> contentMap, final Charset bodyEncoding) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedForm(HttpMethod.POST, contentMap, bodyEncoding, null);
    }

    public HttpResponse executeEncodedFormPost(final Map<String, String> contentMap, final Charset bodyEncoding, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedForm(HttpMethod.POST, contentMap, bodyEncoding, additionalHeaders);
    }

    public HttpResponse executePost(final String content) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePost(ContentType.APPLICATION_JSON.getMimeType(), content);
    }

    public HttpResponse executePost(final String content, final String mimeType) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePost(content, mimeType, Charsets.UTF_8);
    }

    public HttpResponse executePost(final String content, final String mimeType, final Charset bodyEncoding) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePost(content, mimeType, bodyEncoding, null);
    }

    public HttpResponse executePost(final String content, final String mimeType, final Charset bodyEncoding, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final StringEntity entity = new StringEntity(content, ContentType.create(mimeType, bodyEncoding));
        return executePost(entity, additionalHeaders);
    }

    public HttpResponse executePost(final File file) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final FileEntity entity = new FileEntity(file, ContentType.APPLICATION_JSON);
        return executePost(entity, null);
    }

    public HttpResponse executePost(final File file, final String mimeType) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final FileEntity entity = new FileEntity(file, ContentType.create(mimeType, Charsets.UTF_8));
        return executePost(entity, null);
    }

    public HttpResponse executePost(final File file, final String mimeType, final Charset bodyEncoding) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final FileEntity entity = new FileEntity(file, ContentType.create(mimeType, bodyEncoding));
        return executePost(entity, null);
    }

    public HttpResponse executePost(final File file, final String mimeType, final Charset bodyEncoding, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final FileEntity entity = new FileEntity(file, ContentType.create(mimeType, bodyEncoding));
        return executePost(entity, additionalHeaders);
    }

    public HttpResponse executePost(final HttpEntity entity, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final RequestBuilder requestBuilder = createHttpRequest(HttpMethod.POST, additionalHeaders);
        requestBuilder.setEntity(entity);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    public HttpResponse executeEncodedFormPut(final Map<String, String> contentMap) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedFormPut(contentMap, Charsets.UTF_8);
    }

    public HttpResponse executeEncodedFormPut(final Map<String, String> contentMap, final Charset bodyEncoding) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedForm(HttpMethod.PUT, contentMap, bodyEncoding, null);
    }

    public HttpResponse executeEncodedFormPut(final Map<String, String> contentMap, final Charset bodyEncoding, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeEncodedForm(HttpMethod.PUT, contentMap, bodyEncoding, additionalHeaders);
    }

    public HttpResponse executePut(final String content) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePut(content, ContentType.APPLICATION_JSON.getMimeType());
    }

    public HttpResponse executePut(final String content, final String mimeType) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePut(content, mimeType, Charsets.UTF_8);
    }

    public HttpResponse executePut(final String content, final String mimeType, final Charset bodyEncoding) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executePut(content, mimeType, bodyEncoding, null);
    }

    public HttpResponse executePut(final String content, final String mimeType, final Charset bodyEncoding, final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final StringEntity entity = new StringEntity(content, ContentType.create(mimeType, bodyEncoding));
        final RequestBuilder requestBuilder = createHttpRequest(HttpMethod.PUT, additionalHeaders);
        requestBuilder.setEntity(entity);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    public HttpResponse executeDelete() throws IntegrationException, IllegalArgumentException, URISyntaxException {
        return executeDelete(null);
    }

    public HttpResponse executeDelete(final Map<String, String> additionalHeaders) throws IntegrationException, IllegalArgumentException, URISyntaxException {
        final RequestBuilder requestBuilder = createHttpRequest(HttpMethod.DELETE, additionalHeaders);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    private HttpResponse executeEncodedForm(final HttpMethod method, final Map<String, String> contentMap, final Charset bodyEncoding, final Map<String, String> additionalHeaders)
            throws IllegalArgumentException, URISyntaxException, IntegrationException {
        final RequestBuilder requestBuilder = createHttpRequest(method, additionalHeaders);
        final List<NameValuePair> parameters = new ArrayList<>();
        if (contentMap != null && !contentMap.isEmpty()) {
            for (final Entry<String, String> entry : contentMap.entrySet()) {
                final NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                parameters.add(nameValuePair);
            }
        }
        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, bodyEncoding);
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

    public HubRequest addQueryParameter(final String queryParameterName, final String queryParameterValue) {
        this.queryParameters.put(queryParameterName, queryParameterValue);
        return this;
    }

    public HubRequest addQueryParameters(final Map<String, String> queryParameters) {
        this.queryParameters.putAll(queryParameters);
        return this;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE);
    }

}
