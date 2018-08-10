package com.synopsys.integration.hub

import com.synopsys.integration.hub.validator.ApiTokenRestConnectionValidator
import com.synopsys.integration.log.IntLogger
import com.synopsys.integration.log.LogLevel
import com.synopsys.integration.log.PrintStreamIntLogger
import com.synopsys.integration.validator.ValidationResults
import org.junit.Test

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
