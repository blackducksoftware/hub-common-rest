package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.rest.ApiTokenRestConnection
import com.blackducksoftware.integration.hub.rest.ApiTokenRestConnectionBuilder
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

class ApiTokenRestConnectionBuilderTest {
    @Test
    public void testApiTokenRestConnectionBuilderEmpty() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        ApiTokenRestConnectionBuilder builder = new ApiTokenRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger

        try {
            builder.build()
            fail("Should have thrown exception")
        } catch (IllegalStateException e) {
            assert e.getMessage().contains("API_TOKEN = ERROR")
        }
    }

    @Test
    public void testApiTokenRestConnectionBuilder() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiToken = "key"
        ApiTokenRestConnectionBuilder builder = new ApiTokenRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger
        builder.apiToken = apiToken

        ApiTokenRestConnection restConnection = builder.build()
        assert null != restConnection
        assert apiToken == restConnection.hubApiToken
    }

    @Test
    public void testCreateConnection() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiToken = "key"
        ApiTokenRestConnectionBuilder builder = new ApiTokenRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger
        builder.apiToken = apiToken

        ApiTokenRestConnection restConnection = builder.createConnection(null)
        assert null != restConnection
        assert apiToken == restConnection.hubApiToken
    }
}
