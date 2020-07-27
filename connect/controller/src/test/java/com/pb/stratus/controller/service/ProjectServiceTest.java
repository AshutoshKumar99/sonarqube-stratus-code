package com.pb.stratus.controller.service;

import com.pb.stratus.controller.AuthType;
import com.pb.stratus.controller.httpclient.Authentication;
import com.pb.stratus.controller.model.RoutingConfig;
import com.pb.stratus.controller.util.RestUrlExecutor;
import com.pb.stratus.controller.util.RestUrlExecutorImpl;
import com.pb.stratus.controller.wmsprofile.WMSProfile;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;

/**
 * Created by SU019CH on 8/20/2019.
 */
public class ProjectServiceTest {

    ProjectService projectService;
    RestUrlExecutor restUrlExecutor;
	String endpointUrl = "http://stratusopinstal:8080/rest/Spatial/ProjectService";
    @Before
    public void setUp() {

        String apiKey = "testApiKey";
        restUrlExecutor = createMock(RestUrlExecutorImpl.class);
        projectService = new ProjectService(endpointUrl, apiKey, restUrlExecutor);
    }

    @Test
    public void testDescribeRoutingConfig() throws Exception {
        String jsonString = "[\n" +
                "    {\n" +
                "        \"name\": \"/Analyst/NamedRoutingConfigurations/GB_Routing\",\n" +
                "        \"definition\": {\n" +
                "            \"namedResourceType\": \"NamedExternalRoutingConfiguration\",\n" +
                "            \"url\": \"http://stratusopinstal:8080/rest/GetTravelBoundary\",\n" +
                "            \"auth\": {\n" +
                "                \"type\": \"basicAuth\",\n" +
                "                \"user\": \"admin\",\n" +
                "                \"password\": \"admin\"\n" +
                "            },\n" +
                "            \"deployment\": \"SpectrumOnPremiseDeprecatedAPI\",\n" +
                "            \"historicTrafficTimeBucket\": true,\n" +
                "            \"defaultDatabase\": \"GBR_Driving\",\n" +
                "            \"databases\": [\n" +
                "                {\n" +
                "                    \"name\": \"AUS_Driving\",\n" +
                "                    \"enable\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"GBR_Driving\",\n" +
                "                    \"enable\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"GBR_Walking\",\n" +
                "                    \"enable\": true\n" +
                "                }\n" +
                "            ],\n" +
                "            \"defaultTravelDistanceBoundary\": {\n" +
                "                \"unit\": \"meters\",\n" +
                "                \"cost\": \"5,10,15\"\n" +
                "            },\n" +
                "            \"defaultTravelTimeBoundary\": {\n" +
                "                \"unit\": \"minutes\",\n" +
                "                \"cost\": \"5,10,15\"\n" +
                "            },\n" +
                "            \"options\": [\n" +
                "                {\n" +
                "                    \"name\": \"MajorRoads\",\n" +
                "                    \"value\": \"true\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"ReturnIslands\",\n" +
                "                    \"value\": \"T\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"SimplificationFactor\",\n" +
                "                    \"value\": \"0.9\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "]";
        Capture<String> payload = Capture.newInstance(CaptureType.ALL);
        Capture<String> url = Capture.newInstance(CaptureType.ALL);
        reset(restUrlExecutor);
        expect(restUrlExecutor.post(capture(payload), capture(url))).andReturn(jsonString);
        replay(restUrlExecutor);

        RoutingConfig routingConfig = projectService.describeRoutingConfig("/Analyst/NamedRoutingConfigurations/GB_Routing");

        assertEquals(routingConfig.getName(), "/Analyst/NamedRoutingConfigurations/GB_Routing");
        assertEquals(routingConfig.getUrl(), "http://stratusopinstal:8080/rest/GetTravelBoundary");
        assertEquals(routingConfig.getAuthType(), AuthType.basicAuth);
        assertEquals(routingConfig.getUser(), "admin");
        assertEquals(routingConfig.getPassword(), "admin");
        assertEquals(routingConfig.getDeployment().name(), "SpectrumOnPremiseDeprecatedAPI");
        assertTrue(routingConfig.isHistoricTrafficTimeBucket());
        assertEquals(routingConfig.getDefaultDatabase(), "GBR_Driving");
        assertEquals(routingConfig.getDatabases().get(2).getName(), "GBR_Walking");
        assertEquals(routingConfig.getDefaultTravelDistanceBoundary().getUnit(), "meters");
        assertEquals(routingConfig.getDefaultTravelDistanceBoundary().getCost(), "5,10,15");
        assertEquals(routingConfig.getDefaultTravelTimeBoundary().getUnit(), "minutes");
        assertEquals(routingConfig.getDefaultTravelTimeBoundary().getCost(), "5,10,15");
        assertEquals(routingConfig.getOptions().get(2).getName(), "SimplificationFactor");
        assertEquals(routingConfig.getOptions().get(2).getValue(), "0.9");
    }

    @Test
    public void testDescribeWMSConfig() throws Exception {
        String jsonString = "[\n" +
                "    {\n" +
                "        \"name\": \"/AllExternalWMS/WMS07\",\n" +
                "        \"definition\": {\n" +
                "            \"namedResourceType\": \"NamedExternalWMSConfiguration\",\n" +
                "            \"version\": \"1.3.0\",\n" +
                "            \"corsEnabled\": true,\n" +
                "            \"capabilities\": {\n" +
                "                \"url\": \"https://nowcoast.noaa.gov/arcgis/services/nowcoast/mapoverlays_political/MapServer/WMSServer?VERSION=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities\"\n" +
                "            },\n" +
                "            \"map\": {\n" +
                "                \"imageSize\": 256,\n" +
                "                \"url\": \"https://nowcoast.noaa.gov/arcgis/services/nowcoast/mapoverlays_political/MapServer/WmsServer?\",\n" +
                "                \"format\": \"image/jpeg\",\n" +
                "                \"singleImage\": false\n" +
                "            },\n" +
                "            \"auth\": {\n" +
                "                \"type\": \"basicAuth\",\n" +
                "                \"user\": \"admin\",\n" +
                "                \"password\": \"admin\"\n" +
                "            },\n" +
                "            \"extents\": {\n" +
                "                \"projection\": \"epsg:4326\",\n" +
                "                \"left\": 19.191788,\n" +
                "                \"right\": 64.856133,\n" +
                "                \"top\": -66.986389,\n" +
                "                \"bottom\": -165.447891\n" +
                "            },\n" +
                "            \"layers\": [\n" +
                "                {\n" +
                "                    \"name\": \"1\",\n" +
                "                    \"title\": \"USA Urban Area Borders - Scale Band 4\",\n" +
                "                    \"style\": \"default\",\n" +
                "                    \"legendGraphicEnabled\": true,\n" +
                "                    \"featureInfoEnabled\": false,\n" +
                "                    \"legendFormat\": \"image/png\",\n" +
                "                    \"legendUrl\": \"https://nowcoast.noaa.gov/arcgis/services/nowcoast/mapoverlays_political/MapServer/WmsServer?request=GetLegendGraphic%26version=1.3.0%26format=image/png%26layer=1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "]";
        Capture<String> payload = Capture.newInstance(CaptureType.ALL);
        Capture<String> url = Capture.newInstance(CaptureType.ALL);
        reset(restUrlExecutor);
        expect(restUrlExecutor.post(capture(payload), capture(url))).andReturn(jsonString);
        replay(restUrlExecutor);

        WMSProfile wmsProfile = projectService.getWMSProfile("/AllExternalWMS/WMS07");

        assertEquals(wmsProfile.getUrl(), "https://nowcoast.noaa.gov/arcgis/services/nowcoast/mapoverlays_political/MapServer/WMSServer?VERSION=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities");
        assertEquals(wmsProfile.getAuthn(), Authentication.basic);
        assertEquals(wmsProfile.getCredentials().get("username"), "admin");
        assertEquals(wmsProfile.getCredentials().get("password"), "admin");
        assertEquals(wmsProfile.getLegendURLs().get(0), "https://nowcoast.noaa.gov/arcgis/services/nowcoast/mapoverlays_political/MapServer/WmsServer?request=GetLegendGraphic%26version=1.3.0%26format=image/png%26layer=1");
    }

	@Test
	public void testGetSessionTimeout() throws Exception{
		String jsonString = "{\n" +
				"        \"namedResourceType\": \"NamedAnalystConfiguration\",\n" +
				"        \"retainViewOnProjectSwitch\": false,\n" +
				"        \"sessionTimeout\": 30,\n" +
				"        \"defaultBrand\": \"default\"\n" +
				"    }";
		Capture<String> url = Capture.newInstance(CaptureType.ALL);
		reset(restUrlExecutor);
		expect(restUrlExecutor.get(capture(url))).andReturn(jsonString);
		replay(restUrlExecutor);
		int timeout = projectService.getSessionTimeOut();
		assertEquals(timeout, 30);
		assertEquals(url.getValue(), endpointUrl + "/analystConfig.json");

	}
}
