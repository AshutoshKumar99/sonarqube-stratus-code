package com.pb.stratus.controller.action;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A tile service proxy action is a controller action that proxies a request
 * for a tile image to an instance of the Midev tile server. The location
 * of the tile service is read from the controller configuration file. The
 * configuration property name is {@link #TILE_SERVICE_URL_PROPERTY}. The
 * property value is expected to be a fully qualified URL (typically HTTP)
 * pointint to the root path of the tile server web application
 * (e.g. http://tileserver:1234/MapTilingService).
 */
public class TileServiceProxyAction extends BaseControllerAction {
    private static final String NAME_PARAMETER = "name";
    private static final String LEVEL_PARAMETER = "level";
    private static final String ROW_PARAMETER = "row";
    private static final String COL_PARAMETER = "col";
    private static final String OUTPUT_PARAMETER = "output";
    private static final String PNG_PARAMETER_FORMAT = "image/png";
    private static final String GIF_PARAMETER_FORMAT = "image/gif";
    private static final String JPG_PARAMETER_FORMAT = "image/jpg";
    private static final String JPEG_PARAMETER_FORMAT = "image/jpeg";
    private static final String PNG_TILE_FORMAT = ".png";
    private static final String GIF_TILE_FORMAT = ".gif";
    private static final String JPG_TILE_FORMAT = ".jpg";
    //Fix for CONN-15929
    private static final String JPEG_TILE_FORMAT = ".jpeg";

    private HttpRequestExecutorFactory requestExecutorFactory;
    private ClientHttpRequestFactory clientRequestFactory;
    private IHttpRequestExecutor iHttpRequestExecutor;

    public TileServiceProxyAction(HttpRequestExecutorFactory requestExecutorFactory, ClientHttpRequestFactory clientRequestFactory) {
        this.requestExecutorFactory = requestExecutorFactory;
        this.clientRequestFactory = clientRequestFactory;
    }

    private static final Logger log = LogManager
            .getLogger(TileServiceProxyAction.class.getName());

    @Override
    public void init() {
        super.init();
        iHttpRequestExecutor = requestExecutorFactory.create(getConfig());
    }

    private URI createURI(HttpServletRequest request) throws ServletException {

        String mapName = request.getParameter(NAME_PARAMETER);
        if (mapName == null)
            throw new ServletException("Missing map name parameter in the clientRequest");

        String level = request.getParameter(LEVEL_PARAMETER);
        if (level == null)
            throw new ServletException("Missing level parameter in the clientRequest");

        String col = request.getParameter(COL_PARAMETER);
        if (col == null)
            throw new ServletException("Missing col parameter in the clientRequest");

        String row = request.getParameter(ROW_PARAMETER);
        if (row == null)
            throw new ServletException("Missing row parameter in the clientRequest");

        String outputFormat = request.getParameter(OUTPUT_PARAMETER);
        if (outputFormat == null)
            throw new ServletException("Missing output parameter in the clientRequest");

        String tenantName = (String) request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
        if (tenantName == null)
            throw new ServletException("Missing tenant name attribute in the clientRequest");

        if (outputFormat.equals(GIF_PARAMETER_FORMAT))
            outputFormat = GIF_TILE_FORMAT;
        else if (outputFormat.equals(PNG_PARAMETER_FORMAT))
            outputFormat = PNG_TILE_FORMAT;
        else if (outputFormat.equals(JPG_PARAMETER_FORMAT))
            outputFormat = JPG_TILE_FORMAT;
        else if (outputFormat.equals(JPEG_PARAMETER_FORMAT))
            outputFormat = JPEG_TILE_FORMAT;
        else
            throw new ServletException("Invalid output format value");

        try {
            String str = "";
            if (mapName.contains("/")) {
                str = getConfig().getTileServiceUrl().toString()
                        + mapName
                        + "/" + level
                        + "/" + col + ":" + row
                        + "/" + "tile" + outputFormat;
            } else {
                str = getConfig().getTileServiceUrl().toString()
                        + "/" + tenantName.toLowerCase()
                        + "/" + "NamedTiles"
                        + "/" + mapName
                        + "/" + level
                        + "/" + col + ":" + row
                        + "/" + "tile" + outputFormat;
            }
            URI uri = new URI(UriUtils.encodeUri(str, "UTF-8"));
            return uri;
        } catch (URISyntaxException ex) {
            throw new ServletException("Some parameter(s) cannot be used as URI components");
        } catch (java.io.UnsupportedEncodingException unsupportedException) {
            throw new ServletException("Unable to encode the url in utf-8 encoding");
        }
    }

    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
        ClientHttpRequest restRequest = clientRequestFactory.createRequest(createURI(request), HttpMethod.GET);
        ClientHttpResponse restResponse = iHttpRequestExecutor.executeRequest(restRequest);
        HttpStatus status = restResponse.getStatusCode();

        if (status == HttpStatus.OK) {
            response.setContentType(restResponse.getHeaders().getContentType().toString());
            response.setHeader("Cache-Control", "max-age=3600");
            InputStream input = restResponse.getBody();
            OutputStream output = response.getOutputStream();

            try {
                IOUtils.copy(input, output);
            } catch (SocketException e) {
                log.error("Client Abort :" + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            }
        }
    }
}
