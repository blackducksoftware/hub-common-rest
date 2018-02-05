package com.blackducksoftware.integration.hub

import org.apache.http.client.methods.RequestBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.rest.ApiKeyRestConnectionBuilder
import com.blackducksoftware.integration.hub.rest.HttpMethod
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.rest.exception.IntegrationRestException
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ApiKeyRestConnectionTest {

    public static final int CONNECTION_TIMEOUT = 213

    private final MockWebServer server = new MockWebServer();

    @Before public void setUp() throws Exception {
        server.start();
    }

    @After public void tearDown() throws Exception {
        server.shutdown();
    }

    private RestConnection getRestConnection(){
        getRestConnection(new MockResponse().setResponseCode(200))
    }

    private RestConnection getRestConnection(MockResponse response){
        final Dispatcher dispatcher = new Dispatcher() {
                    @Override
                    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                        response
                    }
                };
        server.setDispatcher(dispatcher);
        ApiKeyRestConnectionBuilder builder = new ApiKeyRestConnectionBuilder();
        builder.logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);
        builder.baseUrl = server.url("/")
        builder.timeout = CONNECTION_TIMEOUT
        builder.apiKey = "ApiKey"
        builder.applyProxyInfo(ProxyInfo.NO_PROXY_INFO);
        builder.build()
    }

    private MockResponse getSuccessResponse(){
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("{bearerToken: \"token\"}").setResponseCode(200);
    }

    private MockResponse getUnauthorizedResponse(){
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("{}").setResponseCode(401);
    }

    private MockResponse getFailureResponse(){
        new MockResponse()
                .addHeader("Content-Type", "text/plain")
                .setBody("{}").setResponseCode(404);
    }

    @Test
    public void testHandleExecuteClientCallSuccessful(){
        RestConnection restConnection = getRestConnection(getSuccessResponse())
        RequestBuilder requestBuilder =  restConnection.createRequestBuilder(HttpMethod.GET);
        restConnection.executeRequest(requestBuilder.build()).withCloseable{ assert 200 == it.getStatusCode() }

        assert null != restConnection.getClientBuilder().cookieStore
        assert null != restConnection.getDefaultRequestConfigBuilder().cookieSpec
    }

    @Test
    public void testHandleExecuteClientCallUnauthorized(){
        RestConnection restConnection = getRestConnection(getUnauthorizedResponse())
        RequestBuilder requestBuilder =  restConnection.createRequestBuilder(HttpMethod.GET);
        try{
            restConnection.executeRequest(requestBuilder.build())
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 401 == e.httpStatusCode
        }
    }


    @Test
    public void testHandleExecuteClientCallFail(){
        RestConnection restConnection = getRestConnection(getFailureResponse())
        RequestBuilder requestBuilder =  restConnection.createRequestBuilder(HttpMethod.GET);
        try{
            restConnection.executeRequest(requestBuilder.build())
            fail('Should have thrown exception')
        } catch (IntegrationRestException e) {
            assert 404 == e.httpStatusCode
        }
    }
}
