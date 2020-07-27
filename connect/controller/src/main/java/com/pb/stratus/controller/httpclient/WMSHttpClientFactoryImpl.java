/**
 * Create an CloseableHttpClient object which can be used to make HTTP requests.
 * It reads the authentication information from configuration and sets the credentials  in the client,
 * if authentication method is "basic".
 *
 * User: GU003DU
 * Date: 4/18/13
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */

package com.pb.stratus.controller.httpclient;

import com.pb.stratus.controller.action.ProxyUtils;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.controller.wmsprofile.WMSProfile;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.pb.stratus.controller.settings.Constants.*;


public class WMSHttpClientFactoryImpl implements HttpClientFactory {

    private static final Logger LOGGER = LogManager.getLogger(WMSHttpClientFactoryImpl.class);
    private static final String PARAM_SEPARATOR = "&";
    private MapConfigRepositoryImpl configRepository;

    public WMSHttpClientFactoryImpl(MapConfigRepositoryImpl configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public CloseableHttpClient getHttpClient(String service, boolean secure, StringBuilder request) throws Exception {
        LOGGER.debug("request url" + service);


        CloseableHttpClient httpClient = null;
        if(!secure) {
            httpClient = ProxyUtils.getHttpClient(null, null);
        }
        else {
            // Get the configuration for a secure wms service.
            WMSProfile wmsProfile = this.configRepository.getWMSProfile(service);
            Authentication authentication = wmsProfile.getAuthn();
            LOGGER.debug("Authentication method: " + authentication);

            Map<String, String> credentials = wmsProfile.getCredentials();
            if (authentication == Authentication.basic) {
                httpClient = ProxyUtils.getHttpClient(credentials.get(USER_NAME), credentials.get(PASSWORD));
            } else if (authentication == Authentication.uri_param) {

                Map<String, String> params = new HashMap<String, String>();
                params.put(credentials.get(USER_QUERY_PARAM), credentials.get(USER_NAME));
                params.put(credentials.get(PASSWORD_QUERY_PARAM), credentials.get(PASSWORD));

                // append login parameters
                request.append(PARAM_SEPARATOR);
                request.append(UrlUtil.encode(params));

                httpClient = ProxyUtils.getHttpClient(null, null);
                LOGGER.debug("appended auth information in request url" + request);
            }
        }
        return httpClient;
    }
}
