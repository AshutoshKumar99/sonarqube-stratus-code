package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import net.sf.json.JSONObject;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TileRepositoryTest
{

    private URL baseUrl;
    private ImageReader mockImageReader;
    private TileRepository repo;
    private String tenantName;
    HttpRequestExecutorFactory requestExecutorFactory;
    ClientHttpRequestFactory clientRequestFactory;
    ControllerConfiguration config;
    IHttpRequestExecutor executor;
    ClientHttpRequest clientRequest;
    ClientHttpResponse clientResponse;
    Capture<URI> uriCapture;
    Capture<HttpMethod> httpMethodCapture;
    InputStream stream = new ByteArrayInputStream(new byte[0]);

    JSONObject json;
    String myString;

    @Before
    public void setUp() throws Exception
    {
        mockImageReader = mock(ImageReader.class);
        baseUrl = new URL("file:/some/base/path");
        tenantName = "sometenant";
        requestExecutorFactory = createMock(HttpRequestExecutorFactory.class);
        clientRequestFactory = createMock(ClientHttpRequestFactory.class);
        config = createMock(ControllerConfiguration.class);
        executor = createMock(IHttpRequestExecutor.class);
        clientRequest = createMock(ClientHttpRequest.class);
        clientResponse = createMock(ClientHttpResponse.class);
        myString = "{'Response':{'mapName':'MasterMap','description':'Mastermap' ,"
            + "'coordSys':'epsg:27700','tileWidth':256,'tileHeight':256,"
            + "'bounds':{'maxX':533000,'maxY':190000,'minX':522000,'minY':179000}}}";

        reset(requestExecutorFactory);
        reset(clientRequestFactory);
        reset(config);
        reset(executor);
        reset(clientRequest);
        reset(clientResponse);

    }

    @Test
    public void shouldParseResponseIntoBufferedImage() throws Exception
    {

        URI uri = new URI(baseUrl + "/" + tenantName
            + "/NamedTiles/someLayer/4/3:2/tile.png");
        uriCapture = new Capture<URI>();
        httpMethodCapture = new Capture<HttpMethod>();

        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getTileServiceUrl()).andReturn(baseUrl);
        expect(clientRequestFactory.createRequest(capture(uriCapture),

        capture(httpMethodCapture))).andReturn(clientRequest);
        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(clientResponse.getBody()).andReturn(stream);
        replayAll();
        repo = new TileRepository(tenantName, executor, mockImageReader,
            baseUrl, clientRequestFactory);
        BufferedImage expected = ImageIO.read(TileRepositoryTest.class
            .getResource("tile.png"));
        when(mockImageReader.readFromStream(stream)).thenReturn(expected);
        BufferedImage actual = repo.getTile("someLayer", 1, 2, 3, "image/png");
        assertEquals(HttpMethod.GET, httpMethodCapture.getValue());
        assertEquals(expected, actual);
        assertEquals(uri.toString(), uriCapture.getValue().toString());
    }

    @Test
    public void shouldreturnDecription() throws Exception
    {

        URI uri = new URI(baseUrl + "/" + tenantName
            + "/NamedTiles/someLayer/description.json");
        uriCapture = new Capture<URI>();
        httpMethodCapture = new Capture<HttpMethod>();

        expect(requestExecutorFactory.create(config)).andReturn(executor);
        expect(config.getTileServiceUrl()).andReturn(baseUrl);
        expect(clientRequestFactory.createRequest(capture(uriCapture),

        capture(httpMethodCapture))).andReturn(clientRequest);
        expect(executor.executeRequest(clientRequest)).andReturn(clientResponse);
        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        InputStream stream = new ByteArrayInputStream(myString.getBytes());
        expect(clientResponse.getBody()).andReturn(stream);

        replayAll();
        repo = new TileRepository(tenantName, executor, mockImageReader,
            baseUrl, clientRequestFactory);
        repo.describe("somelayer");

    }

    private void replayAll()
    {
        replay(requestExecutorFactory);
        replay(clientRequestFactory);
        replay(config);
        replay(executor);
        replay(clientRequest);
        replay(clientResponse);
    }
}
