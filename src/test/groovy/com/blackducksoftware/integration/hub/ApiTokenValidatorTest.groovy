package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.validator.ApiTokenValidator
import com.blackducksoftware.integration.validator.ValidationResults

class ApiTokenValidatorTest {
    @Test
    public void testApiTokenValidatorEmpty() {
        ApiTokenValidator validator = new ApiTokenValidator()
        ValidationResults results = validator.assertValid()
        assert null != results
        assert results.hasErrors()
        assert !results.hasWarnings()
        assert !results.isSuccess()
    }

    @Test
    public void testApiTokenValidator() {
        String apiToken = "key"
        ApiTokenValidator validator = new ApiTokenValidator()
        validator.apiToken = apiToken
        ValidationResults results = validator.assertValid()
        assert null != results
        assert !results.hasErrors()
        assert !results.hasWarnings()
        assert results.isSuccess()

        assert apiToken == validator.apiToken
    }
}
