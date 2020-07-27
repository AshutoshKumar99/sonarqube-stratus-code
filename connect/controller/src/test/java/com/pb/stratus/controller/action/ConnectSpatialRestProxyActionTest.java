package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import junit.framework.TestCase;
import org.easymock.Capture;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.easymock.EasyMock.*;

public class ConnectSpatialRestProxyActionTest extends TestCase {
    private static final String URL_PARAMETER = "url";
    HttpRequestExecutorFactory requestExecutorFactory;
    ClientHttpRequestFactory clientRequestFactory;
    ControllerConfiguration config;
    IHttpRequestExecutor executor;
    HttpServletRequest request;
    HttpServletResponse response;
    ClientHttpRequest clientRequest;
    ClientHttpResponse clientResponse;
    HttpHeaders httpHeaders;
    HttpHeaders httpRequestHeaders;

    Capture<URI> uriCapture;
    Capture<HttpMethod> httpMethodCapture;
    URL spatialServiceBaseUrl;

    @Override
    public void setUp() throws MalformedURLException {
        requestExecutorFactory = createMock(HttpRequestExecutorFactory.class);
        clientRequestFactory = createMock(ClientHttpRequestFactory.class);
        config = createMock(ControllerConfiguration.class);
        executor = createMock(IHttpRequestExecutor.class);
        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        clientRequest = createMock(ClientHttpRequest.class);
        clientResponse = createMock(ClientHttpResponse.class);
        httpHeaders = new HttpHeaders();
        httpRequestHeaders = new HttpHeaders();
        uriCapture = new Capture<URI>();
        httpMethodCapture = new Capture<HttpMethod>();
        reset(requestExecutorFactory);
        reset(clientRequestFactory);
        reset(config);
        reset(executor);
        reset(request);
        reset(response);
        reset(clientRequest);
        reset(clientResponse);
    }

    @Override
    public void tearDown() {
        verify(requestExecutorFactory);
        verify(clientRequestFactory);
        verify(config);
        verify(executor);
        verify(request);
        verify(response);
        verify(clientRequest);
        verify(clientResponse);
    }

    public void testExecuteAnalystEnv() throws IOException, ServletException {
        spatialServiceBaseUrl = new URL("http","someHost", 8080, "");
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getSpatialServiceBaseUrl()).andReturn(spatialServiceBaseUrl);
        expect(request.getRequestURL()).andReturn(new StringBuffer("http://localhost:8010/connect/controller/connectProxy/rest/Spatial/MappingService"));
        expect(request.getParameter(URL_PARAMETER)).andReturn("/maps.json");
        expect(request.getParameter("encodeSpecialChars")).andReturn(null);
        expect(request.getMethod()).andReturn(HttpMethod.GET.toString());
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);

        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setHeader("Cache-Control", "max-age=3600");
        response.setStatus(200);
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        replayAll();
        ConnectSpatialRestProxyAction action = new ConnectSpatialRestProxyAction(
                requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("http://someHost:8080/rest/Spatial/MappingService/maps.json",
                uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());

    }

    public void testExecuteAnalystEnvForError() throws IOException, ServletException {
        spatialServiceBaseUrl = new URL("http","someHost", 8080, "");
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getSpatialServiceBaseUrl()).andReturn(spatialServiceBaseUrl);
        expect(request.getRequestURL()).andReturn(new StringBuffer("http://localhost:8010/connect/controller/connectProxy/rest/Spatial/MappingService"));
        expect(request.getParameter(URL_PARAMETER)).andReturn("/maps.json");
        expect(request.getParameter("encodeSpecialChars")).andReturn(null);
        expect(request.getMethod()).andReturn(HttpMethod.GET.toString());
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);

        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.NOT_FOUND);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setHeader("Cache-Control", "max-age=3600");
        response.setStatus(404);
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        replayAll();
        ConnectSpatialRestProxyAction action = new ConnectSpatialRestProxyAction(
                requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("http://someHost:8080/rest/Spatial/MappingService/maps.json",
                uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());

    }


    public void testExecuteStratusEnv() throws IOException, ServletException {
        spatialServiceBaseUrl = new URL("https://stratusgreen-noidp1-int.pbi.global.pvt");
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getSpatialServiceBaseUrl()).andReturn(spatialServiceBaseUrl);
        expect(request.getRequestURL()).andReturn(new StringBuffer("http://localhost:8010/connect/controller/connectProxy/MappingService/services/rest"));
        expect(request.getParameter(URL_PARAMETER)).andReturn("/map.json");
        expect(request.getParameter("encodeSpecialChars")).andReturn("true");
        expect(request.getMethod()).andReturn(HttpMethod.GET.toString());
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);

        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setHeader("Cache-Control", "max-age=3600");

        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
        response.setStatus(200);

        replayAll();
        ConnectSpatialRestProxyAction action = new ConnectSpatialRestProxyAction(
                requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("https://stratusgreen-noidp1-int.pbi.global.pvt/MappingService/services/rest/map.json",
                uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());
    }

    public void testExecuteWithEncodingSpecial() throws IOException, ServletException {
        spatialServiceBaseUrl = new URL("https://stratusgreen-noidp1-int.pbi.global.pvt");
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getSpatialServiceBaseUrl()).andReturn(spatialServiceBaseUrl);
        expect(request.getRequestURL()).andReturn(new StringBuffer("http://localhost:8010/connect/controller/connectProxy/MappingService/services/rest"));
        expect(request.getParameter(URL_PARAMETER)).andReturn("/map.json?q=select * from table where name %3D 'ABC %26 XYZ'");
        expect(request.getParameter("encodeSpecialChars")).andReturn("true");
        expect(request.getMethod()).andReturn(HttpMethod.GET.toString());
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);

        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setHeader("Cache-Control", "max-age=3600");

        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
        response.setStatus(200);

        replayAll();
        ConnectSpatialRestProxyAction action = new ConnectSpatialRestProxyAction(
                requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("https://stratusgreen-noidp1-int.pbi.global.pvt/MappingService/services/rest/map.json?q=select%20*%20from%20table%20where%20name%20%3D%20%27ABC%20%26%20XYZ%27",
                uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());
    }


    private void replayAll() {
        replay(requestExecutorFactory);
        replay(clientRequestFactory);
        replay(config);
        replay(executor);
        replay(request);
        replay(response);
        replay(clientRequest);
        replay(clientResponse);
    }

    private static class DummyServletOutputStream extends ServletOutputStream {
        public void write(int b) throws IOException {
        }
    }
}
