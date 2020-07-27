package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.mapproject.MapProjectGetRequestHandler;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
 * Created by JU002AH on 6/26/2018.
 */
public class MapProjectProxyAction extends BaseControllerAction {

    private final static Logger logger = LogManager.getLogger(CustomTemplateGeneratorAction.class);
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String NO_CACHE = "no-cache";

    private AuthorizationUtils authzUtils;
    private HttpRequestExecutorFactory requestExecutorFactory;
    private ClientHttpRequestFactory clientRequestFactory;
    private IHttpRequestExecutor iHttpRequestExecutor;

    //error messages
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process request,check logs for detailed info";

    private static final String URI_SYNTAX_EXCEPTION = "Some parameter(s) cannot be used as URI components";
    private static final String UNSUPPORTED_ENCODING_EXCEPTION = "Unable to encode the url in utf-8 encoding";
    private MapProjectGetRequestHandler mapProjectGetRequestHandler;
    public MapProjectProxyAction(AuthorizationUtils authzUtils, HttpRequestExecutorFactory requestExecutorFactory, ClientHttpRequestFactory clientRequestFactory) {
        this.authzUtils = authzUtils;
        this.requestExecutorFactory = requestExecutorFactory;
        this.clientRequestFactory = clientRequestFactory;

    }

    @Override
    public void init() {
        super.init();
        iHttpRequestExecutor = requestExecutorFactory.create(getConfig());

        mapProjectGetRequestHandler = new MapProjectGetRequestHandler(this.authzUtils,  clientRequestFactory,
                iHttpRequestExecutor, getConfig());
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InvalidGazetteerException {
        ClientHttpRequest restRequest;
        try {
            String roles = getUserRoles(request);
            if (request.getMethod().toUpperCase().compareTo(Constants.REQUEST_TYPE_GET) == 0) {
                mapProjectGetRequestHandler.executeGet(request, response, false);
                return;
            } else if (request.getMethod().toUpperCase().compareTo(Constants.REQUEST_TYPE_PUT) == 0) {
                restRequest = clientRequestFactory.createRequest(createURI(request, roles), HttpMethod.PUT);
                updatePutRequestData(request, restRequest);
            } else if (request.getMethod().toUpperCase().compareTo(Constants.REQUEST_TYPE_POST) == 0) {
                restRequest = clientRequestFactory.createRequest(createURI(request, roles), HttpMethod.POST);
                updatePostDataRequest(request, restRequest);
            } else if (request.getMethod().toUpperCase().compareTo(Constants.REQUEST_TYPE_DELETE) == 0) {
                restRequest = clientRequestFactory.createRequest(createURI(request, roles), HttpMethod.DELETE);
                updatePostDataRequest(request, restRequest);
            } else {
                restRequest = clientRequestFactory.createRequest(createURI(request, roles), HttpMethod.OPTIONS);
                updatePostDataRequest(request, restRequest);
            }

            ClientHttpResponse restResponse = iHttpRequestExecutor.executeRequest(restRequest);
            HttpStatus status = restResponse.getStatusCode();

                if(restResponse.getHeaders().getContentType() != null){
                    response.setContentType(restResponse.getHeaders().getContentType().toString());
                }
                response.setHeader(CACHE_CONTROL, NO_CACHE);
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

    private void updatePutRequestData(HttpServletRequest request, ClientHttpRequest restRequest) throws ServletException, IOException, InvalidGazetteerException {
        InputStream inputStream = request.getInputStream();
        String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        if (!jsonString.isEmpty()) {
            JSONObject postData = JSONObject.fromObject(jsonString);
            if (postData != null) {
                OutputStream os = restRequest.getBody();
                InputStream is = IOUtils.toInputStream(postData.toString(), StandardCharsets.UTF_8.name());
                try {
                    IOUtils.copy(is, os);
                } finally {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }
            }
            updatePostPutHeaders(restRequest);
        }
    }


    private void updatePostDataRequest(HttpServletRequest request, ClientHttpRequest restRequest) throws ServletException, IOException, InvalidGazetteerException {

        String postData = request.getParameter("postData");
        if (postData != null) {
            OutputStream os = restRequest.getBody();
            InputStream is = IOUtils.toInputStream(postData, StandardCharsets.UTF_8.name());
            try {
                IOUtils.copy(is, os);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(os);
            }
        }
        updatePostPutHeaders(restRequest);
    }

    private void updatePostPutHeaders(ClientHttpRequest restRequest) {
        MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                .APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        restRequest.getHeaders().setContentType(contentTypeHeader);
        restRequest.getHeaders().add("Access-Control-Allow-Origin", "*");
    }


    private URI createURI(HttpServletRequest request, String roles) throws ServletException, MalformedURLException {
        String restBaseUrl = getConfig().getSSAMapProjectBaseUrl().toString();
        String[] arr = request.getRequestURL().toString().split("mapProjectProxy");
        String tenant = (String) (request.getAttribute("tenant"));
        String url = "";
        try {
            // TODO Right now analyst only support super admin concept it will further modify
            // when sub-admin support will be added
            if(roles.contains("admin") ||
                roles.contains("AnalystGuestRole")
                ||
                    request.getRequestURI().contains("UPDATE_MAP_PROJECT_AUTH") || arr[1].split("/").length == 6) {
                if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                    url = restBaseUrl + "/" + tenant + arr[1];
                } else {
                    url = restBaseUrl + "/" + tenant + arr[1] + "/?" + request.getQueryString();
                }
            }
            else {
                if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                    url = restBaseUrl + "/" +
                          tenant + "/" +
                          SecurityContextHolder.getContext().getAuthentication().getPrincipal() +  arr[1];
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
