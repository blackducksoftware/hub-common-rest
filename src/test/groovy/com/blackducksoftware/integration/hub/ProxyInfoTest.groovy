/**
 * Hub Common Rest
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
package com.blackducksoftware.integration.hub

import org.apache.commons.lang3.StringUtils
import org.junit.Test

import com.blackducksoftware.integration.hub.proxy.ProxyInfo

class ProxyInfoTest {

    private static  String VALID_URL = "http://www.google.com"

    @Test
    public void testProxyConstructor() {
        String username1 = null
        String password1 = null
        Credentials credentials1 = null
        String proxyHost1 = null
        int proxyPort1 = 0
        String proxyIgnoredHosts1 = null
        String ntlmDomain = null
        String ntlmWorkstation = null
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, ntlmDomain, ntlmWorkstation)
        assert null == proxyInfo1.host
        assert 0 == proxyInfo1.port
        assert null == proxyInfo1.proxyCredentials
        assert null == proxyInfo1.ignoredProxyHosts
        assert null == proxyInfo1.ntlmDomain
        assert null == proxyInfo1.ntlmWorkstation

        username1 = "username"
        password1 = "password"
        credentials1 = new Credentials(username1, password1);
        proxyHost1 = "proxyHost"
        proxyPort1 = 25
        proxyIgnoredHosts1 = "*"
        ntlmDomain = "domain"
        ntlmWorkstation = "workstation"
        proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, ntlmDomain, ntlmWorkstation)
        String maskedPassword = proxyInfo1.getMaskedPassword()
        assert proxyHost1 == proxyInfo1.host
        assert proxyPort1 == proxyInfo1.port
        assert credentials1 == proxyInfo1.proxyCredentials
        assert proxyIgnoredHosts1 == proxyInfo1.ignoredProxyHosts
        assert ntlmDomain == proxyInfo1.ntlmDomain
        assert ntlmWorkstation == proxyInfo1.ntlmWorkstation

        assert password1 != proxyInfo1.encryptedPassword
        assert password1.length() == proxyInfo1.actualPasswordLength
        assert maskedPassword.length() == password1.length()
        assert password1 != maskedPassword
        assert StringUtils.containsOnly(maskedPassword, "*")
    }

    @Test
    public void testOpenConnection() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = ".*"
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, null, null)

        proxyInfo1.openConnection(new URL(VALID_URL))
    }

    @Test
    public void testShouldUseProxy() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = ""
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, null, null)

        assert true == proxyInfo1.shouldUseProxyForUrl(new URL(VALID_URL))

        proxyIgnoredHosts1 = ".*"
        proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, null, null)
        boolean result = proxyInfo1.shouldUseProxyForUrl(new URL(VALID_URL))
        assert !result
    }

    @Test
    public void testGetProxy() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = ""
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, null, null)
        assert null != proxyInfo1.getProxy(new URL(VALID_URL))

        proxyIgnoredHosts1 = ".*"
        proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1, null, null)
        assert Proxy.NO_PROXY == proxyInfo1.getProxy(new URL(VALID_URL))
    }

    @Test
    public void testHashCode() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = "*"
        String ntlmDomain = "domain"
        String ntlmWorkstation = "workstation"
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1,ntlmDomain,ntlmWorkstation)


        String username2 = "username"
        String password2 = "password"
        Credentials credentials2 = new Credentials(username1, password1);
        String proxyHost2 = "proxyHost"
        int proxyPort2 = 25
        String proxyIgnoredHosts2 = "*"
        String ntlmDomain2 = "domain"
        String ntlmWorkstation2 = "workstation"
        ProxyInfo proxyInfo2 = new ProxyInfo(proxyHost2, proxyPort2, credentials2, proxyIgnoredHosts2,ntlmDomain2,ntlmWorkstation2)

        assert proxyInfo1.hashCode() == proxyInfo2.hashCode()
    }

    @Test
    public void testEquals() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = "*"
        String ntlmDomain = "domain"
        String ntlmWorkstation = "workstation"
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1,ntlmDomain,ntlmWorkstation)


        String username2 = "username"
        String password2 = "password"
        Credentials credentials2 = new Credentials(username1, password1);
        String proxyHost2 = "proxyHost"
        int proxyPort2 = 25
        String proxyIgnoredHosts2 = "*"
        String ntlmDomain2 = "domain"
        String ntlmWorkstation2 = "workstation"
        ProxyInfo proxyInfo2 = new ProxyInfo(proxyHost2, proxyPort2, credentials2, proxyIgnoredHosts2,ntlmDomain2,ntlmWorkstation2)

        assert proxyInfo1.equals(proxyInfo2)
    }

    @Test
    public void testToString() {
        String username1 = "username"
        String password1 = "password"
        Credentials credentials1 = new Credentials(username1, password1);
        String proxyHost1 = "proxyHost"
        int proxyPort1 = 25
        String proxyIgnoredHosts1 = "*"
        String ntlmDomain = "domain"
        String ntlmWorkstation = "workstation"
        ProxyInfo proxyInfo1 = new ProxyInfo(proxyHost1, proxyPort1, credentials1, proxyIgnoredHosts1,ntlmDomain,ntlmWorkstation)


        String username2 = "username"
        String password2 = "password"
        Credentials credentials2 = new Credentials(username1, password1);
        String proxyHost2 = "proxyHost"
        int proxyPort2 = 25
        String proxyIgnoredHosts2 = "*"
        String ntlmDomain2 = "domain"
        String ntlmWorkstation2 = "workstation"
        ProxyInfo proxyInfo2 = new ProxyInfo(proxyHost2, proxyPort2, credentials2, proxyIgnoredHosts2,ntlmDomain2,ntlmWorkstation2)

        assert proxyInfo1.toString() == proxyInfo2.toString()
    }
}
