package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.validator.ApiKeyRestConnectionValidator
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger
import com.blackducksoftware.integration.validator.ValidationResults

class ApiKeyRestConnectionValidatorTest {

    @Test
    public void testApiKeyRestConnectionValidatorEmpty() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        ApiKeyRestConnectionValidator validator = new ApiKeyRestConnectionValidator()
        validator.baseUrl = url
        validator.logger = logger

        ValidationResults results = validator.assertValid()
        assert null != results
        assert results.hasErrors()
        assert !results.hasWarnings()
        assert !results.isSuccess()
    }

    @Test
    public void testApiKeyRestConnectionValidator() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiKey = "key"
        ApiKeyRestConnectionValidator validator = new ApiKeyRestConnectionValidator()
        validator.baseUrl = url
        validator.logger = logger
        validator.apiKey = apiKey

        ValidationResults results = validator.assertValid()
        assert null != results
        assert !results.hasErrors()
        assert !results.hasWarnings()
        assert results.isSuccess()

        assert apiKey == validator.apiKey
    }
}
