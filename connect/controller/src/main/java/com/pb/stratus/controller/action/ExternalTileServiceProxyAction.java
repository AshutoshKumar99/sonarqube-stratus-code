package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.service.ProjectService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.graphdata.utilities.contract.Contract;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by SU019CH on 12/23/2015.
 */
public class ExternalTileServiceProxyAction extends BaseControllerAction {

    private static final Logger LOGGER = LogManager.getLogger(ExternalTileServiceProxyAction.class);
    private static final String CACHE_CONTROL = "Cache-Control";
    /* Maximum cache time is 1 hour*/
    private static final String CACHE_CONTROL_MAX_AGE = "max-age=3600";
    private static final String PARAM_FN_PROFILE = "fnprofile";
    private static final String PARAM_GET_BASE_URL = "MAP_URL";
    private static final String TYPE_REQUEST = "TYPE";
    private static final String PARAM_TILE_SERVICE_PROFILE = "TILE_PROFILE";
    private static final String PARAM_GET_REQUEST_URL = "REQUEST_URL";
    private static final String INVALID_URL = "Invalid Url requested!";
    private static final String INVALID_EXTERNAL_PROFILE = "Invalid external profile requested!";
    private static final String INVALID_FN_PROFILE = "Invalid functionality profile requested!";

    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process current request, please check logs for detailed information!";
    private static final String NOT_AUTHORIZED = "You are not authorized to request this WMS, check with Stratus Administrator.";

    private ProjectService projectService;

    public ExternalTileServiceProxyAction(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InvalidGazetteerException {
        String fnProfile = request.getParameter(PARAM_FN_PROFILE);
        String baseUrl = request.getParameter(PARAM_GET_BASE_URL);
        String requestUrl = request.getParameter(PARAM_GET_REQUEST_URL);
        String type = request.getParameter(TYPE_REQUEST);
        String tileServiceProfile = request.getParameter(PARAM_TILE_SERVICE_PROFILE);

        Contract.pre(!StringUtils.isBlank(requestUrl.trim()), INVALID_URL);

        CloseableHttpClient httpClient = null;
        try {
            validateRequestedURL(baseUrl, fnProfile, type, tileServiceProfile);

            // Execute the given url as HTTP get request.
            httpClient = ProxyUtils.getHttpClient(null, null);

            HttpGet serviceRequest = new HttpGet(requestUrl.trim());
            HttpResponse serviceResponse = httpClient.execute(serviceRequest);

            if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                writeSuccessResponse(serviceResponse, response);
            } else {
                writeFailureResponse(serviceResponse, response);
            }
        } catch (Exception e) {
            LOGGER.error(FAILED_TO_PROCESS_REQUEST, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAILED_TO_PROCESS_REQUEST);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    private void validateRequestedURL(String baseUrl, String fnProfile, String type, String tileServiceProfile) throws Exception {
        Contract.pre(!StringUtils.isBlank(baseUrl), INVALID_URL);

        boolean found = false;
        if ("TMS".equalsIgnoreCase(type) || "XYZ".equalsIgnoreCase(type)) {
            found = validateXYZTMSUrl(baseUrl, tileServiceProfile);
        } else if (type.equalsIgnoreCase("WATERMARK")) {
            found = this.validateWatermarkUrl(baseUrl, fnProfile);
        }
        if (!found) {
            throw new IllegalArgumentException(NOT_AUTHORIZED);
        }
    }

    private boolean validateXYZTMSUrl(String baseUrl, String tileServiceProfile) throws Exception {
        Contract.pre(!StringUtils.isBlank(tileServiceProfile), INVALID_EXTERNAL_PROFILE);
        JSONObject profile = this.getProfileFromSession(tileServiceProfile);
        if (profile == null) {
            String tileProfile = projectService.getThirdPartyProfile(tileServiceProfile);
            JSONArray array = JSONArray.fromObject(tileProfile);
            if (array.isEmpty()) {
                throw new Exception("Did not find the External Tile configuration");
            }
            JSONObject jsonObject = array.getJSONObject(0);
            JSONObject jsonDefObj = jsonObject.getJSONObject("definition");
            this.setProfileInSession(tileServiceProfile, jsonDefObj);
            return this.validateDomainURLs(baseUrl, jsonDefObj);
        } else {
            return this.validateDomainURLs(baseUrl, profile);
        }
    }

    private boolean validateDomainURLs(String baseUrl, JSONObject profile) {
        if (profile.has("url")) {
            String url = profile.getString("url").replace("${z}", "0").replace("${x}", "0").replace("${y}", "0");
            List<String> urls = new ArrayList<>();
            String hostName = URI.create(url).getHost();
            urls.add(hostName);
            if (profile.has("urlSubDomains")) {
                JSONArray urlSubDomains = profile.getJSONArray("urlSubDomains");
                for (Object urlSubDomain : urlSubDomains) {
                    String[] urlParts = hostName.split("\\.");
                    hostName = hostName.replace(urlParts[0], (CharSequence) urlSubDomain);
                    urls.add(hostName);
                }
            }
            boolean valid = false;
            for (String url1 : urls) {
                if (URI.create(baseUrl).getHost().equals(url1)) {
                    valid = true;
                    break;
                }
            }
            return valid;
        } else {
            return false;
        }
    }

    private boolean validateWatermarkUrl(String baseUrl, String fnProfile) throws Exception {
        Contract.pre(!StringUtils.isBlank(fnProfile), INVALID_FN_PROFILE);
        String url = this.getUrlFromSession("WATERMARK:" + fnProfile);
        if (StringUtils.isEmpty(url)) {
            String profile = projectService.getFunctionalityProfile(fnProfile);
            JSONObject jsonDefObj = JSONObject.fromObject(profile).getJSONObject("definition");
            if (jsonDefObj.has("watermark") && jsonDefObj.getJSONObject("watermark").has("url")) {
                url = jsonDefObj.getJSONObject("watermark").getString("url");
                this.setUrlInSession("WATERMARK:" + fnProfile, url);
                return baseUrl.equalsIgnoreCase(url);
            } else {
                return false;
            }
        } else {
            return baseUrl.equalsIgnoreCase(url);
        }
    }

    private void setProfileInSession(String service, JSONObject profile) {
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        requestAttributes.getRequest().getSession().setAttribute(service, profile);
    }

    private void setUrlInSession(String service, String Url) {
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        requestAttributes.getRequest().getSession().setAttribute(service, Url);
    }

    private JSONObject getProfileFromSession(String service) {
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        return (JSONObject) requestAttributes.getRequest().getSession().getAttribute(service);
    }

    private String getUrlFromSession(String service) {
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        return (String) requestAttributes.getRequest().getSession().getAttribute(service);
    }

    /**
     * Send back success response.
     * Override this method if response need to be changed.
     *
     * @param proxyResponse
     * @param response
     * @throws IOException
     */
    protected void writeSuccessResponse(HttpResponse proxyResponse, HttpServletResponse response) throws IOException {

        org.apache.http.Header header = proxyResponse.getEntity().getContentType();
        if (header != null) {
            response.setContentType(header.getValue());
        }
        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_MAX_AGE);
        proxyResponse.getEntity().writeTo(response.getOutputStream());
    }

    /**
     * Send back failure response.
     *
     * @param proxyResponse
     * @param response
     * @throws IOException
     */
    protected void writeFailureResponse(HttpResponse proxyResponse, HttpServletResponse response) throws IOException {
        response.setContentType(proxyResponse.getEntity().getContentType().getValue());
        response.sendError(proxyResponse.getStatusLine().getStatusCode(), IOUtils.toString(proxyResponse.getEntity().getContent()));
    }

}
