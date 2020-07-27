package com.pb.stratus.onpremsecurity.authentication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pb.stratus.onpremsecurity.Constants;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A service to call Spectrum platform server REST API for getting and invalidating a Session Token.
 * Created by GU003DU on 14-Aug-18.
 */
public class SpectrumTokenService {
    private static final Logger logger = Logger.getLogger(SpectrumTokenService.class);
    private IHttpRequestExecutor requestExecutorBasicAuthn;
    private IHttpRequestExecutor requestExecutorTokenAuthn;
    private ClientHttpRequestFactory httpRequestFactory;
    private String url;
    private int ttl;
    private String logoutUrl;

    public SpectrumTokenService(String url, String logoutUrl, Integer ttl,
                                IHttpRequestExecutor requestExecutorBasicAuthn,
                                IHttpRequestExecutor requestExecutorTokenAuthn,
                                ClientHttpRequestFactory httpRequestFactory) {
        this.url = url;
        this.logoutUrl = logoutUrl;
        this.ttl = ttl;
        this.requestExecutorBasicAuthn = requestExecutorBasicAuthn;
        this.requestExecutorTokenAuthn = requestExecutorTokenAuthn;
        this.httpRequestFactory = httpRequestFactory;
    }

    /**
     * Call Spectrum REST API to get a Session token with a TTL of zero.
     * A session token is tied to a user session and can only be used by the computer that requested the token.
     * Since it is tied to a session, the token will become invalid if the session is inactive for 30 (default inactive session interval)minutes.
     * A session token is the most secure type of token and is the recommended token type to use to authenticate
     * to Spectrum™ Technology Platform.
     * To get a session token, use this URL: http://server:port/security/rest/token/access/session/ttlInMinutes
     *
     * @return SpectrumToken A Spectrum token containing user name, token and session identifier.     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public SpectrumToken getSessionToken() throws URISyntaxException, IOException {
        SpectrumToken token = new SpectrumToken();
        String url = this.url + "/" + this.ttl;
        BufferedReader reader = null;
        try {
            URI serviceUri = new URI(url);
            ClientHttpRequest request = httpRequestFactory.createRequest(serviceUri, HttpMethod.GET);
            ClientHttpResponse response = requestExecutorBasicAuthn.executeRequest(request);
            reader = new BufferedReader(new InputStreamReader(response.getBody()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(sb.toString());
            if (elem != null) {
                JsonObject json = elem.getAsJsonObject();
                token.setUserId(json.get(Constants.USER_NAME).getAsString());
                token.setAccessToken(json.get(Constants.ACCESS_TOKEN).getAsString());
                token.setSession(json.get(Constants.SESSION).getAsString());
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return token;
    }

    /**
     * After you are done using a token, you should send a request to the security web service to
     * remove the token from the list of valid tokens maintained on the
     * Spectrum™ Technology Platform server. To logout use the URL: http://server:port/security/rest/token/logout
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public void logout() throws URISyntaxException, IOException {
        URI serviceUri = new URI(logoutUrl);
        ClientHttpRequest request = httpRequestFactory.createRequest(serviceUri, HttpMethod.GET);
        ClientHttpResponse response = requestExecutorTokenAuthn.executeRequest(request);
        if (response.getStatusCode() != HttpStatus.OK) {
            // log a warning
            logger.warn("Token logout request failed, the logout url is: " + serviceUri.toString());
        }
    }
}
