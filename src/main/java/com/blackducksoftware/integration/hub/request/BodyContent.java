package com.blackducksoftware.integration.hub.request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

/**
 * Only one of the body content fields should be set at any one time.
 */
public class BodyContent {
    public static enum BodyContentType {
        FILE,
        MAP,
        STRING,
        OBJECT;
    }

    private final File bodyContentFile;
    private final Map<String, String> bodyContentMap;
    private final String bodyContent;
    private final Object bodyContentObject;

    public BodyContent(final File bodyContentFile) {
        this.bodyContentFile = bodyContentFile;
        this.bodyContentMap = null;
        this.bodyContent = null;
        this.bodyContentObject = null;
    }

    public BodyContent(final Map<String, String> bodyContentMap) {
        this.bodyContentFile = null;
        this.bodyContentMap = bodyContentMap;
        this.bodyContent = null;
        this.bodyContentObject = null;
    }

    public BodyContent(final String bodyContent) {
        this.bodyContentFile = null;
        this.bodyContentMap = null;
        this.bodyContent = bodyContent;
        this.bodyContentObject = null;
    }

    public BodyContent(final Object bodyContentObject) {
        this.bodyContentFile = null;
        this.bodyContentMap = null;
        this.bodyContent = null;
        this.bodyContentObject = bodyContentObject;
    }

    public BodyContentType getBodyContentType() {
        if (bodyContentFile != null) {
            return BodyContentType.FILE;
        } else if (bodyContentMap != null && !bodyContentMap.isEmpty()) {
            return BodyContentType.MAP;
        } else if (StringUtils.isNotBlank(bodyContent)) {
            return BodyContentType.STRING;
        } else if (bodyContentObject != null) {
            return BodyContentType.OBJECT;
        } else {
            return null;
        }
    }

    public HttpEntity createEntity(final Request request, final Gson gson) {
        final BodyContentType bodyContentType = getBodyContentType();

        if (BodyContentType.FILE == bodyContentType) {
            return new FileEntity(getBodyContentFile(), ContentType.create(request.getMimeType(), request.getBodyEncoding()));
        } else if (BodyContentType.MAP == getBodyContentType()) {
            final List<NameValuePair> parameters = new ArrayList<>();
            for (final Entry<String, String> entry : getBodyContentMap().entrySet()) {
                final NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                parameters.add(nameValuePair);
            }
            return new UrlEncodedFormEntity(parameters, request.getBodyEncoding());
        } else if (BodyContentType.STRING == bodyContentType) {
            return new StringEntity(getBodyContent(), ContentType.create(request.getMimeType(), request.getBodyEncoding()));
        } else if (BodyContentType.OBJECT == bodyContentType) {
            return new StringEntity(gson.toJson(getBodyContentObject()), ContentType.create(request.getMimeType(), request.getBodyEncoding()));
        }

        return null;
    }

    public File getBodyContentFile() {
        return bodyContentFile;
    }

    public Map<String, String> getBodyContentMap() {
        return bodyContentMap;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public Object getBodyContentObject() {
        return bodyContentObject;
    }

}
