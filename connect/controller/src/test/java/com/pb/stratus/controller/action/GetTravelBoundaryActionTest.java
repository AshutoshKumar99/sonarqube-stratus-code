package com.pb.stratus.controller.action;

import com.pb.stratus.controller.AuthType;
import com.pb.stratus.controller.DeploymentType;
import com.pb.stratus.controller.geocoder.LIAPIService;
import com.pb.stratus.controller.model.Option;
import com.pb.stratus.controller.model.RoutingConfig;
import com.pb.stratus.controller.service.ProjectService;
import com.pb.stratus.controller.util.RestUrlExecutor;
import com.pb.stratus.controller.util.RestUrlExecutorImpl;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.onpremsecurity.http.HttpRequestAuthorizerFactory;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;

/**
 * Created by NI010GO on 3/7/2016.
 */
public class GetTravelBoundaryActionTest {
    ClientHttpRequestFactory clientRequestFactory;
    HttpServletRequest request;
    HttpServletResponse response;
    ClientHttpRequest clientRequest;
    ClientHttpResponse clientResponse;
    HttpHeaders httpHeaders;
    HttpHeaders httpRequestHeaders;
    InputStream is;
    Capture<URI> uriCapture;
    Capture<HttpMethod> httpMethodCapture;
    GetTravelBoundaryAction getTravelBoundaryAction;
    RestUrlExecutor restUrlExecutor;
    HttpRequestAuthorizerFactory requestAuthorizerFactory;
    RequestAuthorizer requestAuthorizer;
    ControllerConfiguration configuration;
    ProjectService projectService;
    LIAPIService liapiService;
    RoutingConfig routingConfig;

    @Before
    public void setUp() throws Exception {
        initialize();
        resetAll();
        setExpectation();
        replayAll();
    }

    private void initialize() {
        is = createMock(InputStream.class);
        clientRequestFactory = createMock(ClientHttpRequestFactory.class);
        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        clientRequest = createMock(ClientHttpRequest.class);
        clientResponse = createMock(ClientHttpResponse.class);
        restUrlExecutor = createMock(RestUrlExecutorImpl.class);
        requestAuthorizerFactory = createMock(HttpRequestAuthorizerFactory.class);
        requestAuthorizer = createMock(RequestAuthorizer.class);
        configuration = createMock(ControllerConfiguration.class);
        httpHeaders = new HttpHeaders();
        httpRequestHeaders = new HttpHeaders();
        routingConfig = new RoutingConfig();
        projectService = createMock(ProjectService.class);
        liapiService = createMock(LIAPIService.class);
        getTravelBoundaryAction = new GetTravelBoundaryAction(requestAuthorizerFactory, restUrlExecutor,
                projectService, liapiService);
    }

    private void resetAll() {
        reset(clientRequestFactory);
        reset(request);
        reset(response);
        reset(clientRequest);
        reset(clientResponse);
        reset();
        reset(is);
        reset(restUrlExecutor);
        reset(requestAuthorizerFactory);
        reset(requestAuthorizer);
        reset(projectService);
        reset(liapiService);
    }

    private void setExpectation() throws Exception {
        expect(requestAuthorizerFactory.getJWTAuthorizer()).andReturn(requestAuthorizer);
        expect(request.getParameter("url")).andReturn("/Analyst/NamedRoutingConfiguration/driveTimeConfig");
        expect(request.getParameter("longitude")).andReturn("100");
        expect(request.getParameter("latitude")).andReturn("100");
        expect(request.getParameter("coordinateSystem")).andReturn("epsg:3857");
        expect(request.getParameter("routingDatabase")).andReturn("Great Britain");
        expect(request.getParameter("travelCost")).andReturn("1000,2000,3000");
        expect(request.getParameter("travelUnit")).andReturn("m");
        expect(request.getParameter("isDT")).andReturn("true");
        expect(request.getParameter("historicOption")).andReturn("AMPeak");
        expect(clientRequestFactory.createRequest(capture(uriCapture), capture(httpMethodCapture))).andReturn(clientRequest);
        expect(clientResponse.getHeaders()).andReturn(httpHeaders);
        expect(configuration.getSpatialServiceBaseUrl()).andReturn(new URL("http://localhost:8080/rest/Spatial/NamedResourceService"));

        expect(configuration.getProjectServiceUrl()).andReturn(new URL("http://stratusopinstal.pbi.global.pvt:8080/rest/Spatial/ProjectService"));
        expect(configuration.getProjectServiceApiKey()).andReturn("testKey");

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        expect(clientResponse.getBody()).andReturn(new ByteArrayInputStream(new byte[0]));
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());
    }

    private void replayAll() {
        replay(request);
        replay(clientRequestFactory);
        replay(clientRequest);
        replay(is);
    }

    @Test
    public void testExecute() throws Exception {
        getTravelBoundaryAction.setConfig(configuration);

        Capture<String> urlParamCapture = Capture.newInstance(CaptureType.ALL);
        expect(projectService.describeRoutingConfig(capture(urlParamCapture))).andReturn(routingConfig);

        routingConfig.setAuthType(AuthType.basicAuth);
        routingConfig.setUser("admin");
        routingConfig.setPassword("admin123");
        routingConfig.setUrl("http://localhost:8080/rest/GetTravelBoundary");
        List<Option> options = new ArrayList<>();
        options.add(new Option("Option.ReturnIsland", "T"));
        options.add(new Option("Option.ReturnHoles", "T"));
        options.add(new Option("Option.SimplificationFactor", "0.5"));
        routingConfig.setOptions(options);

        Capture<String> userCapture = Capture.newInstance(CaptureType.ALL);
        Capture<String> passwordCapture = Capture.newInstance(CaptureType.ALL);
        expect(requestAuthorizerFactory.getBasicAuthorizer(capture(userCapture), capture(passwordCapture)))
                .andReturn(requestAuthorizer);

        Capture<String> urlCapture = Capture.newInstance(CaptureType.ALL);
        Capture<RequestAuthorizer> authCapture = Capture.newInstance(CaptureType.ALL);
        expect(restUrlExecutor.executeGet(capture(urlCapture), capture(authCapture))).andReturn(clientResponse);

        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        replay(response);
        replay(clientResponse);
        replay(configuration);
        replay(projectService);
        replay(restUrlExecutor);
        replay(requestAuthorizerFactory);

        getTravelBoundaryAction.execute(request, response);

        assertEquals("admin", userCapture.getValue());
        assertEquals("admin123", passwordCapture.getValue());
        assertEquals("http://localhost:8080/rest/GetTravelBoundary/results.json?Data.Longitude=100&Data.Latitude=100&Data.TravelBoundaryCost=1000;2000;3000&Data.TravelBoundaryCostUnits=m&Option.ReturnIsland=T&Option.ReturnHoles=T&Option.CoordinateSystem=epsg:3857&Option.DataSetResourceName=Great%20Britain&Option.BandingStyle=Donut&Option.HistoricTrafficTimeBucket=AMPeak&Option.SimplificationFactor=0.5",
                urlCapture.getValue());
        assertTrue(response.getOutputStream() instanceof DummyServletOutputStream);
    }

    @Test
    public void testExecuteWithoutOptions() throws Exception {
        getTravelBoundaryAction.setConfig(configuration);

        Capture<String> urlParamCapture = Capture.newInstance(CaptureType.ALL);
        expect(projectService.describeRoutingConfig(capture(urlParamCapture))).andReturn(routingConfig);

        routingConfig.setAuthType(AuthType.basicAuth);
        routingConfig.setUser("admin");
        routingConfig.setPassword("admin123");
        routingConfig.setUrl("http://localhost:8080/rest/GetTravelBoundary");

        Capture<String> userCapture = Capture.newInstance(CaptureType.ALL);
        Capture<String> passwordCapture = Capture.newInstance(CaptureType.ALL);
        expect(requestAuthorizerFactory.getBasicAuthorizer(capture(userCapture), capture(passwordCapture)))
                .andReturn(requestAuthorizer);

        Capture<String> urlCapture = Capture.newInstance(CaptureType.ALL);
        Capture<RequestAuthorizer> authCapture = Capture.newInstance(CaptureType.ALL);
        expect(restUrlExecutor.executeGet(capture(urlCapture), capture(authCapture))).andReturn(clientResponse);

        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        replay(response);
        replay(clientResponse);
        replay(configuration);
        replay(projectService);
        replay(restUrlExecutor);
        replay(requestAuthorizerFactory);

        getTravelBoundaryAction.execute(request, response);

        assertEquals("admin", userCapture.getValue());
        assertEquals("admin123", passwordCapture.getValue());
        assertEquals("http://localhost:8080/rest/GetTravelBoundary/results.json?Data.Longitude=100&Data.Latitude=100&Data.TravelBoundaryCost=1000;2000;3000&Data.TravelBoundaryCostUnits=m&Option.CoordinateSystem=epsg:3857&Option.DataSetResourceName=Great%20Britain&Option.BandingStyle=Donut&Option.HistoricTrafficTimeBucket=AMPeak",
                urlCapture.getValue());
        assertTrue(response.getOutputStream() instanceof DummyServletOutputStream);
    }

    @Test
    public void testExecutePBDeveloperAPIs() throws Exception {
        getTravelBoundaryAction.setConfig(configuration);

        Capture<String> urlParamCapture = Capture.newInstance(CaptureType.ALL);
        expect(projectService.describeRoutingConfig(capture(urlParamCapture))).andReturn(routingConfig);

        routingConfig.setAuthType(AuthType.oAuth2);
        routingConfig.setUser("admin");
        routingConfig.setDeployment(DeploymentType.PBDeveloperAPIs);
        routingConfig.setPassword("admin123");
        routingConfig.setUrl("https://api-qa.pitneybowes.com/location-intelligence/geozone/v1/travelboundary");
        List<Option> options = new ArrayList<>();
        options.add(new Option("Option.ReturnIsland", "T"));
        options.add(new Option("Option.ReturnHoles", "T"));
        options.add(new Option("Option.SimplificationFactor", "0.5"));
        routingConfig.setOptions(options);

        Capture<String> userCapture = Capture.newInstance(CaptureType.ALL);
        Capture<String> passwordCapture = Capture.newInstance(CaptureType.ALL);

        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        Capture<URI> uriCapture = Capture.newInstance(CaptureType.ALL);
        HttpResponse httpResponse = createMock(HttpResponse.class);
        expect(liapiService.findTravelBoundary(capture(uriCapture), capture(userCapture), capture(passwordCapture)))
                .andReturn(httpResponse);
        HttpEntity httpEntity = createMock(HttpEntity.class);
        expect(httpResponse.getEntity()).andReturn(httpEntity);

        replay(response);
        replay(clientResponse);
        replay(configuration);
        replay(projectService);
        replay(restUrlExecutor);
        replay(requestAuthorizerFactory);
        replay(liapiService);
        replay(httpResponse);

        getTravelBoundaryAction.execute(request, response);

        assertEquals("admin", userCapture.getValue());
        assertEquals("admin123", passwordCapture.getValue());
        assertEquals("https://api-qa.pitneybowes.com/location-intelligence/geozone/v1/travelboundary/bytime?point=100,100,epsg:3857&costs=1000,2000,3000&costUnit=m&destinationSrs=epsg:3857&db=Great%20Britain&Option.ReturnIsland=T&Option.ReturnHoles=T&Option.SimplificationFactor=0.5",
                uriCapture.getValue().toString());
        assertTrue(response.getOutputStream() instanceof DummyServletOutputStream);
    }

    @Test
    public void testExecuteSpectrumOnPremise() throws Exception {
        getTravelBoundaryAction.setConfig(configuration);

        Capture<String> urlParamCapture = Capture.newInstance(CaptureType.ALL);
        expect(projectService.describeRoutingConfig(capture(urlParamCapture))).andReturn(routingConfig);

        routingConfig.setAuthType(AuthType.basicAuth);
        routingConfig.setUser("admin");
        routingConfig.setDeployment(DeploymentType.SpectrumOnPremise);
        routingConfig.setPassword("admin123");
        routingConfig.setUrl("http://localhost:8080/rest/Spatial/erm/databases");
        List<Option> options = new ArrayList<>();
        options.add(new Option("Option.ReturnIsland", "T"));
        options.add(new Option("Option.ReturnHoles", "T"));
        options.add(new Option("Option.SimplificationFactor", "0.5"));
        routingConfig.setOptions(options);

        Capture<String> userCapture = Capture.newInstance(CaptureType.ALL);
        Capture<String> passwordCapture = Capture.newInstance(CaptureType.ALL);
        expect(requestAuthorizerFactory.getBasicAuthorizer(capture(userCapture), capture(passwordCapture)))
                .andReturn(requestAuthorizer);

        Capture<String> urlCapture = Capture.newInstance(CaptureType.ALL);
        Capture<RequestAuthorizer> authCapture = Capture.newInstance(CaptureType.ALL);
        expect(restUrlExecutor.executeGet(capture(urlCapture), capture(authCapture))).andReturn(clientResponse);

        expect(clientResponse.getStatusCode()).andReturn(HttpStatus.OK);
        expect(response.getOutputStream()).andReturn(new DummyServletOutputStream());

        replay(response);
        replay(clientResponse);
        replay(configuration);
        replay(projectService);
        replay(restUrlExecutor);
        replay(requestAuthorizerFactory);

        getTravelBoundaryAction.execute(request, response);

        assertEquals("admin", userCapture.getValue());
        assertEquals("admin123", passwordCapture.getValue());
        assertEquals("http://localhost:8080/rest/Spatial/erm/databases/Great%20Britain.json?q=travelBoundary&point=100,100,epsg:3857&costs=1000,2000,3000&costUnit=m&destinationSrs=epsg:3857&Option.ReturnIsland=T&Option.ReturnHoles=T&Option.SimplificationFactor=0.5",
                urlCapture.getValue());
        assertTrue(response.getOutputStream() instanceof DummyServletOutputStream);
    }

    private static class DummyServletOutputStream extends ServletOutputStream {
        public void write(int b) throws IOException {
        }
    }
}
