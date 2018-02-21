package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.validator.ApiTokenRestConnectionValidator
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger
import com.blackducksoftware.integration.validator.ValidationResults

class ApiTokenRestConnectionValidatorTest {
    @Test
    public void testApiTokenRestConnectionValidatorEmpty() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        ApiTokenRestConnectionValidator validator = new ApiTokenRestConnectionValidator()
        validator.baseUrl = url
        validator.logger = logger

        ValidationResults results = validator.assertValid()
        assert null != results
        assert results.hasErrors()
        assert !results.hasWarnings()
        assert !results.isSuccess()
    }

    @Test
    public void testApiTokenRestConnectionValidator() {
        String url = "https://github.com"
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO)
        String apiToken = "key"
        ApiTokenRestConnectionValidator validator = new ApiTokenRestConnectionValidator()
        validator.baseUrl = url
        validator.logger = logger
        validator.apiToken = apiToken

        ValidationResults results = validator.assertValid()
        assert null != results
        assert !results.hasErrors()
        assert !results.hasWarnings()
        assert results.isSuccess()

        assert apiToken == validator.apiToken
    }
}
