package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by SU019CH on 4/18/2019.
 */
public class HttpRequestAuthorizerFactoryTest {
    private HttpRequestAuthorizerFactory httpRequestAuthorizerFactory;

    @Before
    public void setup(){
        httpRequestAuthorizerFactory = new HttpRequestAuthorizerFactory();
    }

    @Test
    public void testGetBasicAuthorizer(){
      RequestAuthorizer requestAuthorizer = httpRequestAuthorizerFactory.getBasicAuthorizer("admin", "admin123");
        assertEquals(requestAuthorizer instanceof BasicAuthenticationAuthorizerImpl, true);
    }
}
