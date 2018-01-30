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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;

public class Response implements Closeable {
    private final CloseableHttpResponse response;

    public Response(final CloseableHttpResponse response) {
        this.response = response;
    }

    public Integer getStatusCode() {
        if (response.getStatusLine() != null) {
            return response.getStatusLine().getStatusCode();
        } else {
            return null;
        }
    }

    public String getStatusMessage() {
        if (response.getStatusLine() != null) {
            return response.getStatusLine().getReasonPhrase();
        } else {
            return null;
        }
    }

    public InputStream getContent() throws UnsupportedOperationException, IOException {
        if (response.getEntity() != null) {
            return response.getEntity().getContent();
        } else {
            return null;
        }
    }

    public String getContentString() throws UnsupportedOperationException, IOException {
        return getContentString(Charsets.UTF_8);
    }

    public String getContentString(final Charset encoding) throws UnsupportedOperationException, IOException {
        if (response.getEntity() != null) {
            try (final InputStream inputStream = response.getEntity().getContent()) {
                return IOUtils.toString(inputStream, encoding);
            }
        } else {
            return null;
        }
    }

    public Long getContentLength() {
        if (response.getEntity() != null) {
            return response.getEntity().getContentLength();
        } else {
            return null;
        }
    }

    public String getContentEncoding() {
        if (response.getEntity() != null && response.getEntity().getContentEncoding() != null) {
            return getHeaderValueAsString(response.getEntity().getContentEncoding());
        } else {
            return null;
        }
    }

    public String getContentType() {
        if (response.getEntity() != null && response.getEntity().getContentType() != null) {
            return getHeaderValueAsString(response.getEntity().getContentType());
        } else {
            return null;
        }
    }

    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        for (final Header header : response.getAllHeaders()) {
            headers.put(header.getName(), getHeaderValueAsString(header));
        }
        return headers;
    }

    private String getHeaderValueAsString(final Header header) {
        String value;
        if (header.getElements() != null && header.getElements().length > 0) {
            final List<String> elements = new ArrayList<>();
            for (final HeaderElement headerElement : header.getElements()) {
                elements.add(headerElement.getValue());
            }
            value = StringUtils.join(elements, ",");
        } else {
            value = header.getValue();
        }
        return value;
    }

    public CloseableHttpResponse getActualResponse() {
        return response;
    }

    @Override
    public void close() throws IOException {
        response.close();
    }

}
