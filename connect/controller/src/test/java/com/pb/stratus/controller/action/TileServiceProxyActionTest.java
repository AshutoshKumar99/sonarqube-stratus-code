package com.pb.stratus.controller.action;

import com.pb.stratus.core.common.Constants;
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

public class TileServiceProxyActionTest extends TestCase {
    private static final String NAME_PARAMETER = "name";
    private static final String LEVEL_PARAMETER = "level";
    private static final String ROW_PARAMETER = "row";
    private static final String COL_PARAMETER = "col";
    private static final String OUTPUT_PARAMETER = "output";
    private static final String GIF_PARAMETER_FORMAT = "image/gif";
    private static final String JPEG_PARAMETER_FORMAT = "image/jpeg";
    private static final String JPG_PARAMETER_FORMAT = "image/jpg";

    HttpRequestExecutorFactory requestExecutorFactory;
    ClientHttpRequestFactory clientRequestFactory;
    ControllerConfiguration config;
    IHttpRequestExecutor executor;
    HttpServletRequest request;
    HttpServletResponse response;
    ClientHttpRequest clientRequest;
    ClientHttpResponse clientResponse;
    HttpHeaders httpHeaders;

    Capture<URI> uriCapture;
    Capture<HttpMethod> httpMethodCapture;

    URL tileServiceUrl;

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


        tileServiceUrl = new URL("http","somehost", 8080, "/service" );
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

    public void testExecute() throws IOException, ServletException {
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(request.getParameter(NAME_PARAMETER)).andReturn("nameValue");
        expect(request.getParameter(LEVEL_PARAMETER)).andReturn("10");
        expect(request.getParameter(COL_PARAMETER)).andReturn("59");
        expect(request.getParameter(ROW_PARAMETER)).andReturn("60");
        expect(request.getParameter(OUTPUT_PARAMETER)).andReturn(GIF_PARAMETER_FORMAT);
        expect(request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).andReturn("tenantName");
        expect(config.getTileServiceUrl()).andReturn(tileServiceUrl);
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);
        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        response.setContentType(MediaType.IMAGE_GIF.toString());
        response.setHeader("Cache-Control", "max-age=3600");
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
        httpHeaders.setContentType(MediaType.IMAGE_GIF);

        replayAll();
        TileServiceProxyAction action = new TileServiceProxyAction(requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("http://somehost:8080/service/tenantname/NamedTiles/nameValue/10/59:60/tile.gif",uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());
    }

    public void testExecute_JPG() throws IOException, ServletException {
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(request.getParameter(NAME_PARAMETER)).andReturn("nameValue");
        expect(request.getParameter(LEVEL_PARAMETER)).andReturn("10");
        expect(request.getParameter(COL_PARAMETER)).andReturn("59");
        expect(request.getParameter(ROW_PARAMETER)).andReturn("60");
        expect(request.getParameter(OUTPUT_PARAMETER)).andReturn(JPG_PARAMETER_FORMAT);
        expect(request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).andReturn("tenantName");
        expect(config.getTileServiceUrl()).andReturn(tileServiceUrl);
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);
        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Cache-Control", "max-age=3600");
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        replayAll();
        TileServiceProxyAction action = new TileServiceProxyAction(requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("http://somehost:8080/service/tenantname/NamedTiles/nameValue/10/59:60/tile.jpg",uriCapture.getValue().toString());
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());
    }

    public void testExecute_JPEG() throws IOException, ServletException {
        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(request.getParameter(NAME_PARAMETER)).andReturn("nameValue");
        expect(request.getParameter(LEVEL_PARAMETER)).andReturn("10");
        expect(request.getParameter(COL_PARAMETER)).andReturn("59");
        expect(request.getParameter(ROW_PARAMETER)).andReturn("60");
        expect(request.getParameter(OUTPUT_PARAMETER)).andReturn(JPEG_PARAMETER_FORMAT);
        expect(request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).andReturn("tenantName");
        expect(config.getTileServiceUrl()).andReturn(tileServiceUrl);
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);
        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Cache-Control", "max-age=3600");
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        replayAll();
        TileServiceProxyAction action = new TileServiceProxyAction(requestExecutorFactory, clientRequestFactory);
        action.setConfig(config);
        action.init();
        action.execute(request, response);

        assertEquals("http://somehost:8080/service/tenantname/NamedTiles/nameValue/10/59:60/tile.jpeg",uriCapture.getValue().toString());
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
