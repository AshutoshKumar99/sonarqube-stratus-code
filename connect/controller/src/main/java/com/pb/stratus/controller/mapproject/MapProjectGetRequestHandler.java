package com.pb.stratus.controller.mapproject;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AW001AG on 9/03/2018.
 */
public class MapProjectGetRequestHandler {

    private final static Logger logger = LogManager.getLogger(MapProjectGetRequestHandler.class);
    public static final String MAP_PROJECT_NAME = "mapProjectName";


    private AuthorizationUtils authzUtils;
    private ClientHttpRequestFactory clientRequestFactory;
    private IHttpRequestExecutor iHttpRequestExecutor;

    //error messages
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process request,check logs for detailed info";

    private static final String URI_SYNTAX_EXCEPTION = "Some parameter(s) cannot be used as URI components";
    private static final String UNSUPPORTED_ENCODING_EXCEPTION = "Unable to encode the url in utf-8 encoding";

    private ControllerConfiguration config;

    public MapProjectGetRequestHandler(AuthorizationUtils authzUtils,
                                       ClientHttpRequestFactory clientRequestFactory,
                                       IHttpRequestExecutor iHttpRequestExecutor,
                                       ControllerConfiguration config) {
        this.authzUtils = authzUtils;
        this.clientRequestFactory = clientRequestFactory;
        this.iHttpRequestExecutor = iHttpRequestExecutor;
        this.config = config;
    }


    public void executeGet(HttpServletRequest request, HttpServletResponse response, boolean internalCall)
            throws ServletException, IOException {
        ClientHttpRequest restRequest;
        ClientHttpRequest restRequestProjectsListNonAdmin = null;
        try {
            String roles = getUserRoles(request);

            restRequest = processMapProjectGet(request, internalCall);
            if ((request.getRequestURI().contains(Constants.MAP_PROJECT_LIST_URI) ||
                    request.getParameter(MAP_PROJECT_NAME) != null) ) {

                if (request.getRequestURI().contains(Constants.MAP_PROJECT_LIST_URI) &&
                        !roles.contains("admin") && !roles.contains("AnalystGuestRole")) {
                    // Get users own projects as well
                    restRequestProjectsListNonAdmin = processMapProjectGet(request, true);
                    restRequestProjectsListNonAdmin = appendRoles(restRequestProjectsListNonAdmin, roles);
                }
                restRequest = appendRoles(restRequest, roles);

            }

            ClientHttpResponse restResponse = iHttpRequestExecutor.executeRequest(restRequest);
            if (restRequestProjectsListNonAdmin != null) {
                ClientHttpResponse restResponseNonAdminProjectList =
                        iHttpRequestExecutor.executeRequest(restRequestProjectsListNonAdmin);
                InputStream inputStream = restResponseNonAdminProjectList.getBody();
                String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
                JSONArray jsonObjectList = JSONArray.fromObject(jsonString);

                inputStream = restResponse.getBody();
                jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
                JSONArray jsonObjectList2 = JSONArray.fromObject(jsonString);


                for (int i = 0; i < jsonObjectList2.size(); i++) {
                    jsonObjectList.add(jsonObjectList2.getJSONObject(i));
                }


                sendGetResponse(response, jsonObjectList);
                return;
            }
            HttpStatus status = restResponse.getStatusCode();
            if (status.equals(HttpStatus.OK) && request.getParameter(MAP_PROJECT_NAME) != null) {
                if (status.equals(HttpStatus.OK)) {
                    processGetResponse(request, response, restResponse);
                }

            } else if (status.equals(HttpStatus.NOT_FOUND)) {
                // TO avoid recursive loop
                // Case when actually resource donot exist we need to break and return not exist
                if (!internalCall &&
                        !(roles.contains("admin") ||
                                roles.contains("AnalystGuestRole"))) {
                    executeGet(request, response, true);
                }
            }

            if (restResponse.getHeaders().getContentType() != null) {
                response.setContentType(restResponse.getHeaders().getContentType().toString());
            }
            response.setStatus(status.value());
            InputStream input = restResponse.getBody();
            response.setHeader("Access-Control-Allow-Origin", "*");
            OutputStream output = response.getOutputStream();


            try {
                IOUtils.copy(input, output);
            } catch (SocketException e) {
                logger.error(Constants.NOT_AUTHORIZED, e);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            }

        } catch (Exception e) {
            logger.error(Constants.NOT_AUTHORIZED, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAILED_TO_PROCESS_REQUEST);
        }
    }

    /**
     * Appends role(s) as url parameter and creates new rest request
     *
     * @param restRequest get request
     * @param roles       comma separated roles
     * @return new rest request
     * @throws IOException
     * @throws URISyntaxException
     */
    private ClientHttpRequest appendRoles(ClientHttpRequest restRequest, String roles) throws IOException, URISyntaxException {
        String uriStr = restRequest.getURI().toString();
        if (uriStr.contains("?")) {
            uriStr = uriStr + "&role=" + roles;
        } else {
            uriStr = uriStr + "/?role=" + roles;
        }
        URI uri = new URI(UriUtils.encodeUri(uriStr, "UTF-8"));
        return clientRequestFactory.createRequest(uri, HttpMethod.GET);
    }

    /**
     * Gets user roles of the logged in user
     *
     * @param request
     * @return comma separated user roles
     */
    private String getUserRoles(HttpServletRequest request) {
        Collection<GrantedAuthority> authorities = authzUtils.getUserRoles(request);
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return String.join(",", roles);
    }

    private void processGetResponse(HttpServletRequest request,
                                    HttpServletResponse response, ClientHttpResponse restResponse)
            throws IOException {
        InputStream inputStream = restResponse.getBody();
        String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        sendGetResponse(response, JSONObject.fromObject(jsonString));
    }


    private void sendGetResponse(HttpServletResponse response, Object results)
            throws IOException {
        OutputStream os = response.getOutputStream();
        InputStream is = IOUtils.toInputStream(results.toString(), StandardCharsets.UTF_8.name());
        try {
            IOUtils.copy(is, os);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private ClientHttpRequest processMapProjectGet(HttpServletRequest request,
                                                   boolean searchInUserFolder) throws ServletException, IOException {
        return clientRequestFactory.createRequest(createURI(request, searchInUserFolder), HttpMethod.GET);
    }

    private URI createURI(HttpServletRequest request, boolean searchInUserFolder) throws
            ServletException, MalformedURLException {
        String restBaseUrl = config.getSSAMapProjectBaseUrl().toString();
        String[] arr = request.getRequestURL().toString().split("mapProjectProxy");
        String tenant = (String) (request.getAttribute("tenant"));
        String url;
        try {
            if (!searchInUserFolder) {
                if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                    url = restBaseUrl + "/" + tenant + arr[1];
                } else {
                    url = restBaseUrl + "/" + tenant + arr[1] + "/?" + request.getQueryString();
                }
            } else {
                if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                    url = restBaseUrl + "/" +
                            tenant + "/" +
                            SecurityContextHolder.getContext().getAuthentication().getPrincipal() + arr[1];
                } else {
                    url = restBaseUrl + "/" +
                            tenant + "/" +
                            SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
                            arr[1] +
                            "/?" + request.getQueryString();
                }
            }
            return new URI(UriUtils.encodeUri(url, "UTF-8"));
        } catch (URISyntaxException ex) {
            throw new ServletException(URI_SYNTAX_EXCEPTION);
        } catch (java.io.UnsupportedEncodingException unsupportedException) {
            throw new ServletException(UNSUPPORTED_ENCODING_EXCEPTION);
        }
    }


}
