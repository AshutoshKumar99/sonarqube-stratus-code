package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 4/3/14
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestExecutorImplTest {

    private HttpRequestExecutorImpl target;
    private ClientHttpRequest mockRequest;
    private RequestAuthorizer mockAuthorizer;
    private ClientHttpResponse mockResponse;

    @Before
    public void setup() throws URISyntaxException {
        target = new HttpRequestExecutorImpl();
        mockRequest = mock(ClientHttpRequest.class);
        mockResponse = mock(ClientHttpResponse.class);
        mockAuthorizer = mock(RequestAuthorizer.class);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getURI()).thenReturn(new URI("http://localhost:/xyz"));
        when(mockRequest.getHeaders()).thenReturn(mock(HttpHeaders.class));
        target.setAuthorizer(mockAuthorizer);
    }

    @After
    public void teardown(){

    }

    @Test
    public void testExecuteRequest() throws IOException {
        when(mockAuthorizer.isAuthorized(mockRequest)).thenReturn(false);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockRequest.execute()).thenReturn(mockResponse);
        target.executeRequest(mockRequest);
        verify(mockAuthorizer).authorize(mockRequest);
        verify(mockRequest).execute();

    }

    @Test
    public void testExecuteAuthorisedRequest() throws IOException {
        when(mockAuthorizer.isAuthorized(mockRequest)).thenReturn(true);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockRequest.execute()).thenReturn(mockResponse);
        target.executeRequest(mockRequest);
        verify(mockAuthorizer, times(0)).authorize(mockRequest);
        verify(mockRequest).execute();

    }
}
