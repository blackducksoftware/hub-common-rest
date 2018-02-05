package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.rest.ApiKeyRestConnection
import com.blackducksoftware.integration.hub.rest.ApiKeyRestConnectionBuilder
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

class ApiKeyRestConnectionBuilderTest {

    @Test
    public void testApiKeyRestConnectionBuilderEmpty() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        ApiKeyRestConnectionBuilder builder = new ApiKeyRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger

        try {
            builder.build()
            fail("Should have thrown exception")
        } catch (IllegalStateException e) {
            assert e.getMessage().contains("API_KEY = ERROR")
        }
    }

    @Test
    public void testApiKeyRestConnectionBuilder() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiKey = "key"
        ApiKeyRestConnectionBuilder builder = new ApiKeyRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger
        builder.apiKey = apiKey

        ApiKeyRestConnection restConnection = builder.build()
        assert null != restConnection
        assert apiKey == restConnection.hubApiKey
    }

    @Test
    public void testCreateConnection() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiKey = "key"
        ApiKeyRestConnectionBuilder builder = new ApiKeyRestConnectionBuilder()
        builder.baseUrl = url
        builder.logger = logger
        builder.apiKey = apiKey

        ApiKeyRestConnection restConnection = builder.createConnection(null)
        assert null != restConnection
        assert apiKey == restConnection.hubApiKey
    }
}
