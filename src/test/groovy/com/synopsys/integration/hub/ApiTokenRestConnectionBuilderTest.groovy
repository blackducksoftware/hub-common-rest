package com.synopsys.integration.hub

import com.synopsys.integration.hub.rest.ApiTokenRestConnection
import com.synopsys.integration.hub.rest.ApiTokenRestConnectionBuilder
import com.synopsys.integration.log.IntLogger
import com.synopsys.integration.log.LogLevel
import com.synopsys.integration.log.PrintStreamIntLogger
import org.junit.Test

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
