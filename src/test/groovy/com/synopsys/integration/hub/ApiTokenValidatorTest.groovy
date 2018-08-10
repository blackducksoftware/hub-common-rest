package com.synopsys.integration.hub

import com.synopsys.integration.hub.validator.ApiTokenValidator
import com.synopsys.integration.validator.ValidationResults
import org.junit.Test

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
