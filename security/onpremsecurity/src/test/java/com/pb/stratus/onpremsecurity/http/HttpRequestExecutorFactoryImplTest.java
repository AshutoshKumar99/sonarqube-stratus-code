package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 4/3/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestExecutorFactoryImplTest {

    private HttpRequestExecutorFactoryImpl target;
    private ControllerConfiguration mockConfig;

    @Before
    public void setup(){
        mockConfig = mock(ControllerConfiguration.class);
        target = new HttpRequestExecutorFactoryImpl();
    }

    @After
    public void teardown(){

    }

    @Test
    public void testCreateExecutor(){
        RequestAuthorizer mockAuthorizer = mock(RequestAuthorizer.class);
        target.setRequestAuthorizer(mockAuthorizer);
        IHttpRequestExecutor executor = target.create(mockConfig);
        RequestAuthorizer authorizer = ((HttpRequestExecutorImpl)executor).getAuthorizer();
        assertNotNull(authorizer);
        assertEquals(mockAuthorizer,authorizer);
    }
}
