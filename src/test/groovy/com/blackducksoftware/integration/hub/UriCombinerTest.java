/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.rest.UriCombiner;

public class UriCombinerTest {
    @Test
    public void absolutePathTest() {
        final String url = "http://www.website.com";
        final String path = "/this/path/is/absolute";

        assertPath(url, path, url + path);
    }

    @Test
    public void doubleSlashTest() {
        final String url = "http://www.website.com/";
        final String path = "/leading/slash/here/and/trailing/slash/in/url";

        assertPath(url, path, url + path.substring(1));
    }

    @Test
    public void relativePathTest() {
        final String url = "http://www.website.com";
        final String path = "this/path/is/relative";

        assertPath(url, path, url + "/" + path);
    }

    @Test
    public void nullPathTest() {
        final String url = "http://www.website.com";
        final String path = null;

        assertPath(url, path, url);
    }

    @Test
    public void complexUrlTest() {
        final String url = "http://www.website.com:845?test=thing#stuff";
        final String path = "/complex/url/path";

        assertPath(url, path, "http://www.website.com:845/complex/url/path?test=thing#stuff");
    }

    private void assertPath(final String url, final String path, final String expectedResult) {
        final UriCombiner uriCombiner = new UriCombiner();
        String result = "";
        try {
            result = uriCombiner.pieceTogetherUri(new URL(url), path);
        } catch (MalformedURLException | IntegrationException e) {
            fail(e.getMessage());
        }
        assertEquals(expectedResult, result);
    }

}
