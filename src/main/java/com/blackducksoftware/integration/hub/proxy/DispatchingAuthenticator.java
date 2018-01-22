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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;

import okhttp3.Authenticator;
import okhttp3.Challenge;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class DispatchingAuthenticator implements Authenticator {
    private final Map<String, Authenticator> authenticatorRegistry;

    public DispatchingAuthenticator(final ProxyInfo proxyInfo) throws IllegalArgumentException, EncryptionException {
        authenticatorRegistry = new HashMap<>();
        authenticatorRegistry.put("basic", new BasicAuthenticator(proxyInfo.getUsername(), proxyInfo.getDecryptedPassword()));
        authenticatorRegistry.put("digest", new DigestAuthenticator(new Credentials(proxyInfo.getUsername(), proxyInfo.getDecryptedPassword())));
        authenticatorRegistry.put("ntlm", new NTLMAuthenticator(proxyInfo.getUsername(), proxyInfo.getDecryptedPassword(), proxyInfo.getNtlmDomain(), proxyInfo.getNtlmWorkstation()));
    }

    @Override
    public Request authenticate(final Route route, final Response response) throws IOException {
        final List<Challenge> challenges = response.challenges();
        if (!challenges.isEmpty()) {
            for (final Challenge challenge : challenges) {
                final String scheme = challenge.scheme();
                Authenticator authenticator = null;
                if (scheme != null) {
                    authenticator = authenticatorRegistry.get(scheme.toLowerCase(Locale.getDefault()));
                }
                if (authenticator != null) {
                    return authenticator.authenticate(route, response);
                }
            }
        }
        throw new IllegalArgumentException("Unsupported auth scheme " + challenges);
    }

}
