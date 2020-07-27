package com.pb.stratus.controller.service.invocationhandler;

import com.pb.stratus.controller.ExceptionConverter;
import com.pb.stratus.controller.featuresearch.Target;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExceptionConvertingInvocationHandlerTest
{
    
    private ExceptionConverter mockConverter;
    
    private Target mockTarget;
    
    private Target proxy;

    @Before
    public void setUp()
    {
        mockConverter = mock(ExceptionConverter.class);
        mockTarget = mock(Target.class);
        ExceptionConvertingInvocationHandler handler
                = new ExceptionConvertingInvocationHandler(mockConverter, 
                        mockTarget);
        proxy = (Target) Proxy.newProxyInstance(getClass().getClassLoader(), 
                new Class<?>[] {Target.class}, handler);
    }
    
    @Test
    public void shouldConvertExceptions() throws Exception
    {
        RuntimeException mockException = new RuntimeException();
        IOException originalException = new IOException();
        when(mockConverter.convert(originalException)).thenReturn(
                mockException);
        when(mockTarget.invoke(any(Object.class))).thenThrow(originalException);
        try
        {
            proxy.invoke(null);
            fail("No exception thrown");
        }
        catch (Exception x)
        {
            assertEquals(mockException, x);
        }
        verify(mockTarget).invoke(any(Object.class));
    }
    
    @Test
    public void shouldPassThroughParamsAndReturnValue() throws Exception
    {
        Object mockRetVal = new Object();
        Object mockParam = new Object();
        when(mockTarget.invoke(mockParam)).thenReturn(mockRetVal);
        Object actualRetVal = proxy.invoke(mockParam);
        assertEquals(mockRetVal, actualRetVal);
    }
    
//    @Test
//    public void shouldAsdf() throws Exception
//    {
//        FeatureServiceInterface mockService = mock(FeatureServiceInterface.class);
//        when(mockService.describeTable(any(DescribeTableRequest.class))).thenThrow(new ServiceException("asdfasdf", new MapInfoDeveloperException()));
//        ExceptionConvertingInvocationHandler handler = new ExceptionConvertingInvocationHandler(new FeatureServiceExceptionConverter(), mockService);
//        FeatureServiceInterface proxy = (FeatureServiceInterface) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {FeatureServiceInterface.class}, handler);
//        proxy.describeTable(null);
//    }

}
