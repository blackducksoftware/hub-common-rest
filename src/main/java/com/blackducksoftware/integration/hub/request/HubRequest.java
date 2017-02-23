/**
 * Hub Rest Common
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException;
import com.google.gson.JsonObject;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Most usages of the Hub endpoints as of 2016-11-23 (Hub 3.3.1) should use the HubPagedRequest, but there are several
 * REST endpoints
 * that do not consume limit or offset, and those should use this implementation.
 */
public class HubRequest {
    private final RestConnection restConnection;

    private String url;

    private final List<String> urlSegments = new ArrayList<>();

    private final Map<String, String> queryParameters = new HashMap<>();

    private String q;

    public HubRequest(final RestConnection restConnection) {
        this.restConnection = restConnection;
    }

    public JsonObject executeGetForResponseJson() throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createGetRequest(httpUrl);
            response = restConnection.handleExecuteClientCall(request);
            final String responseString = response.body().string();
            final JsonObject jsonObject = restConnection.getJsonParser().parse(responseString).getAsJsonObject();
            return jsonObject;
        } catch (final IOException e) {
            throw new IntegrationRestException(response.code(), response.message(),
                    "There was a problem getting this item : " + httpUrl.uri().toString() + ". Error : " + e.getMessage(), e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String executeGetForResponseString() throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createGetRequest(httpUrl);
            response = restConnection.handleExecuteClientCall(request);
            return response.body().string();
        } catch (final IOException e) {
            throw new IntegrationRestException(response.code(), response.message(),
                    "There was a problem getting this item : " + httpUrl.uri().toString() + ". Error : " + e.getMessage(), e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String executePost(final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createPostRequest(httpUrl, restConnection.createJsonRequestBody(content));
            response = restConnection.handleExecuteClientCall(request);
            return response.header("location");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String executePost(final String mediaType, final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createPostRequest(httpUrl, restConnection.createJsonRequestBody(mediaType, content));
            response = restConnection.handleExecuteClientCall(request);
            return response.header("location");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void executePut(final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createPutRequest(httpUrl, restConnection.createJsonRequestBody(content));
            response = restConnection.handleExecuteClientCall(request);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void executePut(final String mediaType, final String content) throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        Response response = null;
        try {
            final Request request = restConnection.createPutRequest(httpUrl, restConnection.createJsonRequestBody(mediaType, content));
            response = restConnection.handleExecuteClientCall(request);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void executeDelete() throws IntegrationException {
        final HttpUrl httpUrl = buildHttpUrl();
        final Request request = restConnection.createDeleteRequest(httpUrl);
        restConnection.handleExecuteClientCall(request);
    }

    public void populateQueryParameters() {
        if (StringUtils.isNotBlank(q)) {
            queryParameters.put(QUERY_Q, q);
        }
    }

    private HttpUrl buildHttpUrl() {
        populateQueryParameters();
        if (StringUtils.isBlank(url)) {
            url = restConnection.getHubBaseUrl().toString();
        }
        return restConnection.createHttpUrl(url, urlSegments, queryParameters);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public List<String> getUrlSegments() {
        return urlSegments;
    }

    public void addUrlSegment(final String urlSegment) {
        urlSegments.add(urlSegment);
    }

    public void addUrlSegments(final List<String> urlSegment) {
        urlSegments.addAll(urlSegment);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public HubRequest addQueryParameter(final String queryParameterName, final String queryParameterValue) {
        queryParameters.put(queryParameterName, queryParameterValue);
        return this;
    }

    public HubRequest addQueryParameters(final Map<String, String> queryParameters) {
        queryParameters.putAll(queryParameters);
        return this;
    }

    public String getQ() {
        return q;
    }

    public void setQ(final String q) {
        this.q = q;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE);
    }

}
