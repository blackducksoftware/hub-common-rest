/**
 * hub-common-rest
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
package com.synopsys.integration.hub.validator;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.hub.ApiTokenField;
import com.synopsys.integration.validator.AbstractValidator;
import com.synopsys.integration.validator.ValidationResult;
import com.synopsys.integration.validator.ValidationResultEnum;
import com.synopsys.integration.validator.ValidationResults;

public class ApiTokenValidator extends AbstractValidator {
    private String apiToken;

    @Override
    public ValidationResults assertValid() {
        final ValidationResults result = new ValidationResults();

        validateCredentials(result);

        return result;
    }

    public void validateCredentials(final ValidationResults result) {
        validateApiToken(result);
    }

    public void validateApiToken(final ValidationResults result) {
        if (StringUtils.isBlank(apiToken)) {
            result.addResult(ApiTokenField.API_TOKEN, new ValidationResult(ValidationResultEnum.ERROR, "No Hub API token was found."));
        }
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
    }

}
