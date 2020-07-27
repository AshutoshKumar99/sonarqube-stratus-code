package com.pb.stratus.core.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 5/19/14
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletRequestAttributes.class, OgnlPropertyEvaluatorTest.class})
public class OgnlPropertyEvaluatorTest {

    private OgnlPropertyEvaluator target;
    private ServletRequestAttributes mockAttributes;
    private HttpServletRequest mockRequest;

    @Before
    public void setup() throws Exception {
        target = new OgnlPropertyEvaluator();
        mockRequest = mock(HttpServletRequest.class);

    }

    public void testInsertLocaleFunction(){
        mockAttributes = createMock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);
        RequestContextHolder.setRequestAttributes(mockAttributes);
        replay(mockAttributes);
        when(mockRequest.getParameter("lang")).thenReturn("fr");
        String result = target.evaluate("insertLocale(\"?\") ");
        verify(mockAttributes);
    }

}
