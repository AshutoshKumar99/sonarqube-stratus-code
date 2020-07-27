package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
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
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Builds Rest URL for saas and analyst profiles for feature and mapping webservices
 * User: AR009SH
 */
public class ConnectSpatialRestProxyAction extends BaseControllerAction {

    private static final String ACTION_PATH_INFO = "connectProxy";
    private static final String URL_PARAMETER = "url";
    private static final String POST_DATA_PARAMETER = "postData";
    private static final String URL_ENCODING = "UTF-8";
    private static final String REQUEST_TYPE_POST = "POST";
    private static final String REQUEST_TYPE_PUT = "PUT";
    private static final String REQUEST_TYPE_OPTIONS = "OPTIONS";
    private static final String REQUEST_TYPE_DELETE = "DELETE";
    private static final String CACHE_PROPERTY = "Cache-Control";
    private static final String CACHE_AGE = "max-age=3600";
    private static final String DATA = "data=";
    private static final int DATA_COUNT = 5; // "data="
    private static final String ACCESS_CONTROL_ACL = "/AccessControlService/acl/resources";

    //error messages
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process request,check logs for detailed info";
    private static final String URI_SYNTAX_EXCEPTION = "Some parameter(s) cannot be used as URI components";
    private static final String UNSUPPORTED_ENCODING_EXCEPTION = "Unable to encode the url in utf-8 encoding";
    private static final String MISSING_URL_PARAM = "Missing url parameter in the clientRequest";
    private static final Logger log = LogManager
            .getLogger(ConnectSpatialRestProxyAction.class.getName());
    private HttpRequestExecutorFactory requestExecutorFactory;
    private ClientHttpRequestFactory clientRequestFactory;
    private IHttpRequestExecutor iHttpRequestExecutor;

    public ConnectSpatialRestProxyAction(HttpRequestExecutorFactory requestExecutorFactory, ClientHttpRequestFactory clientRequestFactory) {
        this.requestExecutorFactory = requestExecutorFactory;
        this.clientRequestFactory = clientRequestFactory;
    }

    @Override
    public void init() {
        super.init();
        iHttpRequestExecutor = requestExecutorFactory.create(getConfig());
    }

    private URI createURI(HttpServletRequest request, String requestMethod) throws ServletException, MalformedURLException {

        String[] arr = request.getRequestURL().toString().split(ACTION_PATH_INFO);

        String url = request.getParameter(URL_PARAMETER);
        if (url == null &&
                (requestMethod.compareTo(REQUEST_TYPE_PUT) == 0
                        || requestMethod.compareTo(REQUEST_TYPE_DELETE) == 0 ||
                        request.getRequestURI().indexOf(ACCESS_CONTROL_ACL) > -1)) {
            return createPUTURI(request);
        } else if (url == null) {
            throw new ServletException(MISSING_URL_PARAM);
        }

        try {
            if (!url.startsWith("/") && !arr[1].endsWith("/")) {
                url = "/" + url;
            }

            String restBaseUrl = getConfig().getSpatialServiceBaseUrl().toString();
            //For Queries with custom filter, we need to encode special characters like &,#
            /*
             For CONN-30578, encodedSpecialChars is sent for SSA 12 to reduce impact but
             moving forward this will be removed and encoding this way will be generic.
             */
            String encodeSpecialChars = request.getParameter("encodeSpecialChars");
            if (encodeSpecialChars != null && Boolean.parseBoolean(encodeSpecialChars)) {

                String completeUrl = restBaseUrl + arr[1] + url;
                String[] fragments = completeUrl.split("\\?");
                String encodedUrl = addQueryStringToUrlString(fragments);
                return new URI(encodedUrl);
            } else {
                return new URI(UriUtils.encodeUri(restBaseUrl + arr[1] + url, URL_ENCODING));
            }

        } catch (URISyntaxException ex) {
            throw new ServletException(URI_SYNTAX_EXCEPTION);
        } catch (java.io.UnsupportedEncodingException unsupportedException) {
            throw new ServletException(UNSUPPORTED_ENCODING_EXCEPTION);
        }
    }

    /**
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    private URI createPUTURI(HttpServletRequest request) throws ServletException, MalformedURLException {
        String restBaseUrl = getConfig().getSpatialServiceBaseUrl().toString();
        String[] arr = request.getRequestURL().toString().split(ACTION_PATH_INFO);
        String url = "";
        try {
            if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                url = restBaseUrl + arr[1];
            } else {
                url = restBaseUrl + arr[1] + "/?" + request.getQueryString();
            }
            return new URI(UriUtils.encodeUri(url, URL_ENCODING));
        } catch (URISyntaxException ex) {
            throw new ServletException(URI_SYNTAX_EXCEPTION);
        } catch (java.io.UnsupportedEncodingException unsupportedException) {
            throw new ServletException(UNSUPPORTED_ENCODING_EXCEPTION);
        }
    }


    /*
     *   encode special characters in query parameters.
     */
    private String addQueryStringToUrlString(String[] fragments) throws UnsupportedEncodingException {

        if (fragments.length > 1) {
            String[] parameters = fragments[1].split("&");
            if (parameters == null) {
                return fragments[0];
            }
            for (String parameter : parameters) {
                String[] keyValueArr = parameter.split("=");
                final String encodedKey = URLEncoder.encode(keyValueArr[0], URL_ENCODING);
                String encodedValue = "";
                //connect-web is sending encoded values for &, =, ? to make out a difference between separator, assignment and actual filter values.
                keyValueArr[1] = keyValueArr[1].replaceAll("%26", "&").replaceAll("%3F", "?").replaceAll("%3D", "=");
                encodedValue = URLEncoder.encode(keyValueArr[1], URL_ENCODING).replaceAll("\\+", "%20");

                if (!fragments[0].contains("?")) {
                    fragments[0] += "?" + encodedKey + "=" + encodedValue;
                } else {
                    fragments[0] += "&" + encodedKey + "=" + encodedValue;
                }
            }
        }
        return fragments[0];
    }

    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
        ClientHttpRequest restRequest;
        String requestMethod = request.getMethod().toUpperCase();
        try {
            if (REQUEST_TYPE_POST.compareTo(requestMethod) == 0) {

                if (request.getParameter("methodType") != null && request.getParameter("methodType").equals(REQUEST_TYPE_DELETE)) {
                    //handling of HTTP DELETE requests
                    restRequest = clientRequestFactory.createRequest(createURI(request, requestMethod), HttpMethod.DELETE);
                    MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                            .APPLICATION_JSON.getSubtype());
                    restRequest.getHeaders().setContentType(contentTypeHeader);
                    List acceptMediaTypes = new ArrayList<MediaType>();
                    acceptMediaTypes.add(contentTypeHeader);
                    restRequest.getHeaders().setAccept(acceptMediaTypes);
                } else {
                    restRequest = clientRequestFactory.createRequest(createURI(request, requestMethod), HttpMethod.POST);

                    String postData = request.getParameter(POST_DATA_PARAMETER);
                    if (postData == null) {
                        updatePutRequestData(request, restRequest);
                    }
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
                    MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                            .APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
                    restRequest.getHeaders().setContentType(contentTypeHeader);
                }
            } else if (REQUEST_TYPE_PUT.compareTo(requestMethod) == 0) {
                restRequest = clientRequestFactory.createRequest(createURI(request, requestMethod), HttpMethod.PUT);
                updatePutRequestData(request, restRequest);
                MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                        .APPLICATION_JSON.getSubtype());
                restRequest.getHeaders().setContentType(contentTypeHeader);
                List acceptMediaTypes = new ArrayList<MediaType>();
                acceptMediaTypes.add(contentTypeHeader);
                restRequest.getHeaders().setAccept(acceptMediaTypes);
            } else if (REQUEST_TYPE_OPTIONS.compareTo(requestMethod) == 0) {
                restRequest = clientRequestFactory.createRequest(createOptionsURI(request), HttpMethod.OPTIONS);
                updatePostDataRequest(request, restRequest);
            } else if (REQUEST_TYPE_DELETE.compareTo(requestMethod) == 0) {
                restRequest = clientRequestFactory.createRequest(createURI(request, requestMethod), HttpMethod.DELETE);
            } else {
                restRequest = clientRequestFactory.createRequest(createURI(request, requestMethod), HttpMethod.GET);
            }

            ClientHttpResponse restResponse = iHttpRequestExecutor.executeRequest(restRequest);
            HttpStatus status = restResponse.getStatusCode();
            if (requestMethod.compareTo(REQUEST_TYPE_OPTIONS) != 0) {
                MediaType contentType = restResponse.getHeaders().getContentType();
                if (contentType != null) {//  if there is no content type header
                    response.setContentType(contentType.toString());
                }
            }
            response.setHeader(CACHE_PROPERTY, CACHE_AGE);
            response.setStatus(status.value());
            InputStream input = restResponse.getBody();
            OutputStream output = response.getOutputStream();

            try {
                IOUtils.copy(input, output);
            } catch (SocketException e) {
                log.error("URL: " + request.getRequestURL() + ", query string: " + request.getQueryString(), e);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            }
        } catch (Exception e) {
            log.error(FAILED_TO_PROCESS_REQUEST + ", URL: " + request.getRequestURL()
                    + ", query string: " + request.getQueryString(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAILED_TO_PROCESS_REQUEST);
        }
    }

    /**
     * @param request
     * @param restRequest
     * @throws ServletException
     * @throws IOException
     * @throws InvalidGazetteerException
     */
    private void updatePutRequestData(HttpServletRequest request, ClientHttpRequest restRequest) throws ServletException, IOException, InvalidGazetteerException {
        InputStream inputStream = request.getInputStream();
        String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        if (!jsonString.isEmpty() && jsonString.indexOf(DATA) > -1) {
            jsonString = jsonString.substring(DATA_COUNT, jsonString.length());
            jsonString = UriUtils.decode(jsonString, "UTF-8");
        }
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
        }
    }

    /**
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    private URI createOptionsURI(HttpServletRequest request) throws ServletException, MalformedURLException {
        String restBaseUrl = getConfig().getSpatialServiceBaseUrl().toString();
        String[] arr = request.getRequestURL().toString().split(ACTION_PATH_INFO);
        String tenant = (String) (request.getAttribute("tenant"));
        String url = "";
        try {
            if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
                url = restBaseUrl + '/' + tenant + arr[1];
            } else {
                url = restBaseUrl + '/' + tenant + arr[1] + "/?" + request.getQueryString();
            }
            return new URI(UriUtils.encodeUri(url, URL_ENCODING));
        } catch (URISyntaxException ex) {
            throw new ServletException(URI_SYNTAX_EXCEPTION);
        } catch (java.io.UnsupportedEncodingException unsupportedException) {
            throw new ServletException(UNSUPPORTED_ENCODING_EXCEPTION);
        }
    }

    /**
     * @param request
     * @param restRequest
     * @throws ServletException
     * @throws IOException
     */
    private void updatePostDataRequest(HttpServletRequest request, ClientHttpRequest restRequest) throws ServletException, IOException {

        String postData = request.getParameter(POST_DATA_PARAMETER);
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
    }
}
