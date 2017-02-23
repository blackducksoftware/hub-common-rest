/**
 * Hub Common
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

import java.util.List;

import com.blackducksoftware.integration.hub.rest.RestConnection;

public class HubRequestFactory {
    private final RestConnection restConnection;

    public HubRequestFactory(final RestConnection restConnection) {
        this.restConnection = restConnection;
    }

    public HubRequest createGetRequest(final List<String> urlSegments) {
        final HubRequest hubRequest = new HubRequest(restConnection);
        hubRequest.addUrlSegments(urlSegments);
        return hubRequest;
    }

    public HubRequest createGetRequest(final String url) {
        final HubRequest hubRequest = new HubRequest(restConnection);
        hubRequest.setUrl(url);
        return hubRequest;
    }

    public HubPagedRequest createGetPagedRequest(final List<String> urlSegments) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(100);
        hubPagedRequest.addUrlSegments(urlSegments);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final int itemsPerPage, final List<String> urlSegments) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(itemsPerPage);
        hubPagedRequest.addUrlSegments(urlSegments);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final List<String> urlSegments, final String q) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(100);
        hubPagedRequest.addUrlSegments(urlSegments);
        hubPagedRequest.setQ(q);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final int itemsPerPage, final List<String> urlSegments, final String q) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(itemsPerPage);
        hubPagedRequest.addUrlSegments(urlSegments);
        hubPagedRequest.setQ(q);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final String url) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(100);
        hubPagedRequest.setUrl(url);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final int itemsPerPage, final String url) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(itemsPerPage);
        hubPagedRequest.setUrl(url);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final String url, final String q) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(100);
        hubPagedRequest.setUrl(url);
        hubPagedRequest.setQ(q);
        return hubPagedRequest;
    }

    public HubPagedRequest createGetPagedRequest(final int itemsPerPage, final String url, final String q) {
        final HubPagedRequest hubPagedRequest = new HubPagedRequest(restConnection);
        hubPagedRequest.setLimit(itemsPerPage);
        hubPagedRequest.setUrl(url);
        hubPagedRequest.setQ(q);
        return hubPagedRequest;
    }

    public HubRequest createPostRequest(final List<String> urlSegments) {
        final HubRequest hubRequest = new HubRequest(restConnection);
        hubRequest.addUrlSegments(urlSegments);
        return hubRequest;
    }

    public HubRequest createRequest(final String url) {
        final HubRequest hubRequest = new HubRequest(restConnection);
        hubRequest.setUrl(url);
        return hubRequest;
    }

}
