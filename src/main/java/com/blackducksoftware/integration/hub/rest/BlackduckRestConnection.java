package com.blackducksoftware.integration.hub.rest;

import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.log.IntLogger;

public abstract class BlackduckRestConnection extends RestConnection {
    public BlackduckRestConnection(final IntLogger logger, final URL baseUrl, final int timeout, final ProxyInfo proxyInfo) {
        super(logger, baseUrl, timeout, proxyInfo);
    }

    @Override
    public abstract void addBuilderAuthentication() throws IntegrationException;

    @Override
    public abstract void clientAuthenticate() throws IntegrationException;

}
