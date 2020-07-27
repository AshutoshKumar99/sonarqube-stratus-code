package com.pb.stratus.controller.action;

import com.pb.stratus.controller.DeploymentType;
import com.pb.stratus.controller.geocoder.LIAPIService;
import com.pb.stratus.controller.model.Option;
import com.pb.stratus.controller.model.RoutingConfig;
import com.pb.stratus.controller.service.ProjectService;
import com.pb.stratus.controller.util.RestUrlExecutor;
import com.pb.stratus.onpremsecurity.http.HttpRequestAuthorizerFactory;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by NI010GO on 3/7/2016.
 */
public class GetTravelBoundaryAction extends BaseControllerAction {
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process request, check logs for detailed info";
    private static final String URL_PARAMETER = "url";
    private static final String URI_SYNTAX_EXCEPTION = "Some parameter(s) cannot be used as URI components";
    private static final Logger log = LogManager.getLogger(GetTravelBoundaryAction.class.getName());
    private RestUrlExecutor restUrlExecutor;
    private HttpRequestAuthorizerFactory httpRequestAuthorizerFactory;
    private ProjectService projectService;
    private LIAPIService liapiService;

    public GetTravelBoundaryAction(HttpRequestAuthorizerFactory httpRequestAuthorizerFactory,
                                   RestUrlExecutor restUrlExecutor, ProjectService projectService,
                                   LIAPIService liapiService) {
        super();
        this.restUrlExecutor = restUrlExecutor;
        this.httpRequestAuthorizerFactory = httpRequestAuthorizerFactory;
        this.projectService = projectService;
        this.liapiService = liapiService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // The value of URL_PARAMETER should be routing config name Eg: /Analyst/NamedRoutingConfiguration/driveTimeConfig
            RoutingConfig routingConfig = projectService.describeRoutingConfig(request.getParameter(URL_PARAMETER));

            try {
                URI uri;
                DeploymentType deploymentType = routingConfig.getDeployment();
                RequestAuthorizer requestAuthorizer;
                if (deploymentType == DeploymentType.PBDeveloperAPIs) {
                    uri = createURI(request, routingConfig);
                    if (log.isDebugEnabled()) {
                        log.debug(uri.toString());
                    }
                    HttpResponse serviceResponse = liapiService.findTravelBoundary(uri, routingConfig.getUser(),
                            routingConfig.getPassword());
                    OutputStream output = response.getOutputStream();
                    try {
                        serviceResponse.getEntity().writeTo(output);
                    } finally {
                        IOUtils.closeQuietly(output);
                    }
                } else {
                    if (deploymentType == DeploymentType.SpectrumOnPremise) {
                        uri = createURI(request, routingConfig);
                    } else {
                        uri = createURIForDeprecratedAPI(request, routingConfig);
                    }
                    requestAuthorizer = httpRequestAuthorizerFactory.getBasicAuthorizer(routingConfig.getUser(),
                            routingConfig.getPassword());
                    if (log.isDebugEnabled()) {
                        log.debug(uri.toString());
                    }
                    ClientHttpResponse restResponse = restUrlExecutor.executeGet(uri.toString(), requestAuthorizer);
                    HttpStatus status = restResponse.getStatusCode();

                    if (status == HttpStatus.OK || status == HttpStatus.BAD_REQUEST) {
                        response.setContentType(restResponse.getHeaders().getContentType().toString());
                        InputStream input = restResponse.getBody();
                        OutputStream output = response.getOutputStream();

                        try {
                            IOUtils.copy(input, output);
                        } finally {
                            IOUtils.closeQuietly(input);
                            IOUtils.closeQuietly(output);
                        }
                    } else {
                        response.sendError(restResponse.getStatusCode().value(), restResponse.getStatusText());
                    }
                }
            } catch (Exception e) {
                log.error(FAILED_TO_PROCESS_REQUEST, e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAILED_TO_PROCESS_REQUEST);
            }
        } catch (URISyntaxException e) {
            log.error(URI_SYNTAX_EXCEPTION, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, URI_SYNTAX_EXCEPTION);
        } catch (Exception e) {
            log.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private URI createURI(HttpServletRequest request, RoutingConfig routingConfig) {
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String coordinateSystem = request.getParameter("coordinateSystem");
        String routingDatabase = request.getParameter("routingDatabase");
        String travelCost = request.getParameter("travelCost");
        String travelUnit = getMappedUnit(request.getParameter("travelUnit"));
        String isDT = request.getParameter("isDT");

        String url = routingConfig.getUrl();
        url = url.endsWith("/") ? url : url + "/";
        if (routingConfig.getDeployment() == DeploymentType.SpectrumOnPremise) {
            url += routingDatabase + ".json?q=travelBoundary&";
        } else if (routingConfig.getDeployment() == DeploymentType.PBDeveloperAPIs) {
            if ("true".equals(isDT)) {
                url += "bytime?";
            } else {
                url += "bydistance?";
            }
        }
        url += "point=" + longitude + "," + latitude + "," + coordinateSystem;
        url += "&costs=" + travelCost;
        url += "&costUnit=" + travelUnit;
        url += "&destinationSrs=" + coordinateSystem;

        if (routingDatabase != null && !StringUtils.isEmpty(routingDatabase)
                && routingConfig.getDeployment() == DeploymentType.PBDeveloperAPIs) {
            url += "&db=" + routingDatabase;
        } else if (routingConfig.getDefaultDatabase() != null && StringUtils.isEmpty(routingConfig.getDefaultDatabase())
                && routingConfig.getDeployment() == DeploymentType.PBDeveloperAPIs) {
            url += "&db=" + routingConfig.getDefaultDatabase();
        }

        if (routingConfig.getOptions() != null && !routingConfig.getOptions().isEmpty()) {
            for (Option option : routingConfig.getOptions()) {
                url += "&" + option.getName() + "=" + option.getValue();
            }
        }

        URI uri = null;
        try {
            uri = new URI(UriUtils.encodeUri(url, "UTF-8"));
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            log.error(e);
        }
        return uri;
    }

    private URI createURIForDeprecratedAPI(HttpServletRequest request, RoutingConfig routingConfig) {

        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String coordinateSystem = request.getParameter("coordinateSystem");
        String routingDatabase = request.getParameter("routingDatabase");
        String travelCosts = request.getParameter("travelCost");
        String travelUnits = request.getParameter("travelUnit");

        String url = routingConfig.getUrl();
        url = url.endsWith("/") ? url : url + "/";
        url += "results.json?";
        url += "Data.Longitude=" + longitude + "&Data.Latitude=" + latitude;
        if (travelCosts != null) {
            url += "&Data.TravelBoundaryCost=" + travelCosts.replace(',', ';');
        }
        if (travelUnits != null) {
            url += "&Data.TravelBoundaryCostUnits=" + travelUnits;
        }

        Map<String, String> options = new HashMap<>();

        if (routingConfig.getOptions() != null && !routingConfig.getOptions().isEmpty()) {
            for (Option option : routingConfig.getOptions()) {
                options.put(option.getName(), option.getValue());
            }
        }

        if (coordinateSystem != null) {
            options.put("Option.CoordinateSystem", coordinateSystem);
        }
        if (routingDatabase != null) {
            options.put("Option.DataSetResourceName", routingDatabase);
        } else {
            options.put("Option.DataSetResourceName", routingConfig.getDefaultDatabase());
        }
        // Use brandingStyle provided in config, in case it is provided
        if (options.get("Option.BandingStyle") == null) {
            options.put("Option.BandingStyle", "Donut");
        }

        //Applying some more factors for detailed polygons - @Guru and Sikhar: As per discussion with Mus, we are removing these hard coding as it was overriding the default settings for ERM in Spectrum
        //url += "&Option.SimplificationFactor=1.0&Option.ReturnIslands=true&Option.ReturnHoles=true&Option.MajorRoads=false";
        String historicOption = request.getParameter("historicOption");

        if (historicOption != null) {
            options.put("Option.HistoricTrafficTimeBucket", historicOption);
        }

        Iterator<Map.Entry<String, String>> it = options.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> option = it.next();
            url += "&" + option.getKey() + "=" + option.getValue();
        }

        URI uri = null;
        try {
            uri = new URI(UriUtils.encodeUri(url, "UTF-8"));
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            log.error(e);
        }
        return uri;
    }

    private String getMappedUnit(String unit) {
        switch (unit) {
            case "Feet":
                return "ft";
            case "Hours":
                return "h";
            case "Kilometers":
                return "km";
            case "Meters":
                return "m";
            case "Miles":
                return "mi";
            case "Minutes":
                return "min";
            case "Seconds":
                return "s";
            case "Milliseconds":
                return "msec";
            case "Yards":
                return "yd";
        }
        return unit;
    }
}
