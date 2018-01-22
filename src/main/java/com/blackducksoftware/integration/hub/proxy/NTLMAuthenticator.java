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
package com.blackducksoftware.integration.hub.proxy;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class NTLMAuthenticator extends IntegrationAuthenticator {
    private final NTLMEngineImpl engine = new NTLMEngineImpl();

    private final String username;
    private final String password;
    private final String domain;
    private final String workspace;

    public NTLMAuthenticator(final String username, final String password, final String domain, final String workspace) {
        this.username = username;
        this.password = password;
        this.domain = domain;
        this.workspace = workspace;
    }

    @Override
    public Request authenticate(final Route route, final Response response) throws IOException {
        final String headerKey = getResponseHeader(route);
        final List<String> wwwAuthenticate = response.headers().values(IntegrationAuthenticator.WWW_AUTH);

        if (wwwAuthenticate.contains("NTLM")) {
            String ntlmMsg1 = null;
            try {
                ntlmMsg1 = engine.generateType1Msg(domain, workspace);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return response.request().newBuilder().header(headerKey, "NTLM " + ntlmMsg1).build();
        }
        String ntlmMsg3 = null;
        try {
            ntlmMsg3 = engine.generateType3Msg(username, password, domain, workspace, wwwAuthenticate.get(0).substring(5));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return response.request().newBuilder().header("Authorization", "NTLM " + ntlmMsg3).build();
    }

}
