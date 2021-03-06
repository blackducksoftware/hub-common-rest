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
package com.synopsys.integration.blackduck.rest;

import com.synopsys.integration.blackduck.validator.ApiTokenRestConnectionValidator;
import com.synopsys.integration.rest.connection.AbstractRestConnectionBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.validator.AbstractValidator;

public class ApiTokenRestConnectionBuilder extends AbstractRestConnectionBuilder<ApiTokenRestConnection> {
    private String apiToken;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public AbstractValidator createValidator() {
        final ApiTokenRestConnectionValidator validator = new ApiTokenRestConnectionValidator();
        validator.setBaseUrl(getBaseUrl());
        validator.setTimeout(getTimeout());
        validator.setApiToken(getApiToken());
        validator.setProxyHost(getProxyHost());
        validator.setProxyPort(getProxyPort());
        validator.setProxyUsername(getProxyUsername());
        validator.setProxyPassword(getProxyPassword());
        validator.setProxyIgnoreHosts(getProxyIgnoreHosts());
        validator.setProxyNtlmDomain(getProxyNtlmDomain());
        validator.setProxyNtlmWorkstation(getProxyNtlmWorkstation());
        validator.setLogger(getLogger());
        validator.setCommonRequestHeaders(getCommonRequestHeaders());
        return validator;
    }

    @Override
    public ApiTokenRestConnection createConnection(final ProxyInfo proxyInfo) {
        final ApiTokenRestConnection connection = new ApiTokenRestConnection(getLogger(), getBaseConnectionUrl(), getApiToken(), getTimeout(), proxyInfo);
        return connection;
    }

}
