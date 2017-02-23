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
package com.blackducksoftware.integration.hub.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.item.HubPagedResponse;
import com.blackducksoftware.integration.hub.api.item.HubResponse;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HubParameterizedRequestService<T extends HubResponse> extends HubRequestService {
    private final Class<T> clazz;

    public HubParameterizedRequestService(final RestConnection restConnection, final Class<T> clazz) {
        super(restConnection);
        this.clazz = clazz;
    }

    public HubPagedResponse<T> getPagedResponse(final HubPagedRequest hubPagedRequest) throws IntegrationException {
        final JsonObject jsonObject = hubPagedRequest.executeGetForResponseJson();
        final int totalCount = jsonObject.get("totalCount").getAsInt();
        final List<T> items = getItems(jsonObject);
        return new HubPagedResponse<>(totalCount, items);
    }

    public List<T> getItems(final HubPagedRequest hubPagedRequest) throws IntegrationException {
        final JsonObject jsonObject = hubPagedRequest.executeGetForResponseJson();
        final List<T> items = getItems(jsonObject);
        return items;
    }

    /**
     * This method can be overridden by subclasses to provide special treatment for extracting the items from the
     * jsonObject.
     */
    public List<T> getItems(final JsonObject jsonObject) {
        final LinkedList<T> itemList = new LinkedList<>();
        final JsonElement itemsElement = jsonObject.get("items");
        final JsonArray itemsArray = itemsElement.getAsJsonArray();
        final int count = itemsArray.size();
        for (int index = 0; index < count; index++) {
            final JsonElement element = itemsArray.get(index);
            final T item = getItem(element, clazz);
            itemList.add(item);
        }
        return itemList;
    }

    public List<T> getAllItems(final HubPagedRequest hubPagedRequest) throws IntegrationException {
        final List<T> allItems = new ArrayList<>();

        final HubPagedResponse<T> firstPage = getPagedResponse(hubPagedRequest);
        final int totalCount = firstPage.getTotalCount();
        final List<T> items = firstPage.getItems();
        allItems.addAll(items);

        while (allItems.size() < totalCount) {
            final int currentOffset = hubPagedRequest.getOffset();
            final int increasedOffset = currentOffset + items.size();

            hubPagedRequest.setOffset(increasedOffset);
            final HubPagedResponse<T> nextPage = getPagedResponse(hubPagedRequest);
            allItems.addAll(nextPage.getItems());
        }

        return allItems;
    }

    public List<T> getAllItems(final List<String> urlSegments) throws IntegrationException {
        final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(urlSegments);
        return getAllItems(hubPagedRequest);
    }

    public List<T> getAllItems(final String url) throws IntegrationException {
        final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(url);
        return getAllItems(hubPagedRequest);
    }

    public T getItem(final HubRequest hubRequest) throws IntegrationException {
        return getItem(hubRequest, clazz);
    }

    public T getItem(final String url) throws IntegrationException {
        return getItem(url, clazz);
    }

}
