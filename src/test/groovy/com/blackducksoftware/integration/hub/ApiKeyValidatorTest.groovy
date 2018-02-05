package com.blackducksoftware.integration.hub

import org.junit.Test

import com.blackducksoftware.integration.hub.validator.ApiKeyValidator
import com.blackducksoftware.integration.validator.ValidationResults

class ApiKeyValidatorTest {

    @Test
    public void testApiKeyValidatorEmpty() {
        ApiKeyValidator validator = new ApiKeyValidator()
        ValidationResults results = validator.assertValid()
        assert null != results
        assert results.hasErrors()
        assert !results.hasWarnings()
        assert !results.isSuccess()
    }

    @Test
    public void testApiKeyValidator() {
        String apiKey = "key"
        ApiKeyValidator validator = new ApiKeyValidator()
        validator.apiKey = apiKey
        ValidationResults results = validator.assertValid()
        assert null != results
        assert !results.hasErrors()
        assert !results.hasWarnings()
        assert results.isSuccess()

        assert apiKey == validator.apiKey
    }
}
