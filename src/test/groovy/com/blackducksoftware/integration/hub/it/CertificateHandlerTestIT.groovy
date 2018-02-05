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
package com.blackducksoftware.integration.hub.it

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.security.cert.Certificate

import org.apache.commons.lang3.StringUtils
import org.junit.AfterClass
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.TemporaryFolder

import com.blackducksoftware.integration.IntegrationTest
import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.certificate.CertificateHandler
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */

@Category(IntegrationTest.class)
class CertificateHandlerTestIT {
    private static final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE)

    private static final CertificateHandler CERT_HANDLER = new CertificateHandler(logger, null)
    private static URL url
    private static Certificate originalCertificate

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @BeforeClass
    public static void init() throws Exception {
        RestConnectionTestHelper restConnectionTestHelper = new RestConnectionTestHelper()
        final String urlString = restConnectionTestHelper.getProperty("TEST_HTTPS_HUB_SERVER_URL")
        logger.info("Using Hub server ${urlString}")
        url = new URL(urlString)
        try {
            final boolean isCertificateInKeystore = CERT_HANDLER.isCertificateInTrustStore(url)
            if (isCertificateInKeystore) {
                originalCertificate = CERT_HANDLER.retrieveHttpsCertificateFromTrustStore(url)
                CERT_HANDLER.removeHttpsCertificate(url)
            } else {
                logger.error(String.format("Certificate for %s is not in the keystore.", url.getHost()))
            }
        } catch (final IntegrationException e) {
            logger.error(e.getMessage())
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (originalCertificate != null) {
            CERT_HANDLER.importHttpsCertificate(url, originalCertificate)
        }
    }

    @Test
    public void testCertificateRetrieval() throws Exception {
        final CertificateHandler certificateHandler = new CertificateHandler(logger, null)
        final Certificate output = certificateHandler.retrieveHttpsCertificateFromURL(url)
        assertNotNull(output)
    }

    @Test
    public void testRetrieveAndImportHttpsCertificate() throws Exception {
        final CertificateHandler certificateHandler = new CertificateHandler(logger, null)
        certificateHandler.retrieveAndImportHttpsCertificate(url)
        assertTrue(certificateHandler.isCertificateInTrustStore(url))
        assertNotNull(certificateHandler.retrieveHttpsCertificateFromTrustStore(url))
        certificateHandler.removeHttpsCertificate(url)
        assertFalse(certificateHandler.isCertificateInTrustStore(url))
    }

    @Test
    public void testKeystoreSetBySystemProperty() throws Exception {
        final File tmpTrustStore = folder.newFile()
        assertTrue(tmpTrustStore.length() == 0)
        try {
            System.setProperty("javax.net.ssl.trustStore", tmpTrustStore.getAbsolutePath())

            final CertificateHandler certificateHandler = new CertificateHandler(logger, null)
            certificateHandler.retrieveAndImportHttpsCertificate(url)
            assertTrue(certificateHandler.isCertificateInTrustStore(url))
            assertNotNull(certificateHandler.retrieveHttpsCertificateFromTrustStore(url))
            assertTrue(tmpTrustStore.isFile())
            assertTrue(tmpTrustStore.length() > 0)
        } finally {
            if (tmpTrustStore.exists()) {
                tmpTrustStore.delete()
            }
        }
    }

    @Test
    public void testRetrieveAndImportHttpsCertificateForSpecificJavaHome() throws Exception {
        final String javaHomeToManipulate = System.getProperty("JAVA_TO_MANIPULATE")
        Assume.assumeTrue(StringUtils.isNotBlank(javaHomeToManipulate))

        final CertificateHandler certificateHandlerDefault = new CertificateHandler(logger, null)
        final CertificateHandler certificateHandler = new CertificateHandler(logger, new File(javaHomeToManipulate))

        Certificate original = null
        if (certificateHandler.isCertificateInTrustStore(url)) {
            original = certificateHandler.retrieveHttpsCertificateFromTrustStore(url)
            certificateHandler.removeHttpsCertificate(url)
        }

        try {
            assertFalse(certificateHandler.isCertificateInTrustStore(url))
            assertFalse(certificateHandlerDefault.isCertificateInTrustStore(url))

            certificateHandler.retrieveAndImportHttpsCertificate(url)

            assertTrue(certificateHandler.isCertificateInTrustStore(url))
            assertFalse(certificateHandlerDefault.isCertificateInTrustStore(url))

            certificateHandler.removeHttpsCertificate(url)
        } finally {
            if (original != null) {
                certificateHandler.importHttpsCertificate(url, original)
            }
        }
    }
}
