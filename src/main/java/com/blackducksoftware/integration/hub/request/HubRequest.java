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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.ws.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;

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

    private RequestBuilder createHttpRequest(final HttpMethod method)
            throws IllegalArgumentException, URISyntaxException, IntegrationException {
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
        final RequestBuilder requestBuilder = restConnection.getRequestBuilder(method);
        requestBuilder.setUri(uriBuilder.build());
        return requestBuilder;
    }

    public Response executeGet() throws IntegrationException {
        final RequestBuilder requestBuilder = createHttpRequest(HttpMethod.GET);
        final HttpUriRequest request = requestBuilder.build();
        return restConnection.createResponse(request);
    }

    public Response executeGet(final String mediaType) throws IntegrationException {
        final HttpUriRequest httpUrl = buildHttpUrl();
        final Request request = restConnection.createGetRequest(httpUrl, mediaType);
        return restConnection.createResponse(request);
    }

    public Response executeEncodedFormPost(final Map<String, String> contentMap) throws IntegrationException {
        final HttpUriRequest httpUrl = buildHttpUrl();
        final Request request = restConnection.createPostRequest(httpUrl, restConnection.createEncodedFormBody(contentMap));
        return restConnection.createResponse(request);
    }

    public Response executePost(final String content) throws IntegrationException {
        final HttpUriRequest httpUrl = buildHttpUrl();
        final Request request = restConnection.createPostRequest(httpUrl, restConnection.createJsonRequestBody(content));
        return restConnection.createResponse(request);
    }

    public Response executePost(final String mediaType, final String content) throws IntegrationException {
        final HttpUriRequest httpUrl = buildHttpUrl();
        final Request request = restConnection.createPostRequest(httpUrl, restConnection.createJsonRequestBody(mediaType, content));
        return restConnection.createResponse(request);
    }

    public Response executePost(final String mediaType, final File file) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createPostRequest(httpUrl, restConnection.createFileRequestBody(mediaType, file));
        return restConnection.createResponse(request);
    }

    public Response executeEncodedFormPut(final Map<String, String> contentMap) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createPutRequest(httpUrl, restConnection.createEncodedFormBody(contentMap));
        return restConnection.createResponse(request);
    }

    public Response executePut(final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createPutRequest(httpUrl, restConnection.createJsonRequestBody(content));
        return restConnection.createResponse(request);
    }

    public Response executePut(final String mediaType, final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createPutRequest(httpUrl, restConnection.createJsonRequestBody(mediaType, content));
        return restConnection.createResponse(request);
    }

    public void executeDelete() throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createDeleteRequest(httpUrl);
        try (Response response = restConnection.createResponse(request)) {

        }
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
