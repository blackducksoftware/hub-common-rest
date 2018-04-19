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
package com.blackducksoftware.integration.hub.rest;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationException;

public class UriCombiner implements Serializable {
    private static final long serialVersionUID = 5393401177396012061L;

    public String pieceTogetherUri(final URL baseUrl, final String path) throws IntegrationException {
        try {
            final URI baseUri = baseUrl.toURI();
            final URI combinedUri = new URI(baseUri.getScheme(), null, baseUri.getHost(), baseUri.getPort(), getAsAbsolutePath(path), baseUri.getQuery(), baseUri.getFragment());
            return combinedUri.toString();
        } catch (final URISyntaxException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    private String getAsAbsolutePath(final String path) {
        if (StringUtils.isNotBlank(path) && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

}
