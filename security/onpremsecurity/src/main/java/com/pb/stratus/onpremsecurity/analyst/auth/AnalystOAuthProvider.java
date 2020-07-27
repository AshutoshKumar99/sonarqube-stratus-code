package com.pb.stratus.onpremsecurity.analyst.auth;

import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author ekiras
 */
public class AnalystOAuthProvider {

    public AnalystOAuthAuthentication getAuthToken(String url, String apiKey, String secret)throws RestClientException{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((apiKey + ":" + secret).getBytes(StandardCharsets.UTF_8)));
        HttpEntity httpEntity = new HttpEntity(AnalystOAuthConstants.POST_DATA, httpHeaders);

        ResponseEntity<AnalystOAuthAuthentication> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, AnalystOAuthAuthentication.class);
        return responseEntity.getBody();
    }




}
