package com.pb.stratus.controller.service.invocationhandler;

import com.pb.stratus.controller.featuresearch.Target;
import com.pb.stratus.controller.util.TransactionMonitor;
import com.pb.stratus.core.util.ServiceLoggingUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceLoggingUtil.class)
@PowerMockIgnore("javax.management.*")
public class LoggingInvocationHandlerTest
{
    

    private Target mockTarget;
    
    private Target proxy;

    private TransactionMonitor mockMonitor;

    @Before
    public void setUp()
    {
        mockTarget = mock(Target.class);
        mockMonitor = mock(TransactionMonitor.class);
        proxy= (Target)LoggingInvocationHandler.newInstance( mockTarget, mockMonitor);

    }
    
    @Test
    public void shouldLogTransaction() throws Exception
    {

        proxy.invoke(new Object());
        verify(mockMonitor).logTransaction();
    }

    @Test
    public void verifyContextUpdate()throws Exception{
        mockStatic(ServiceLoggingUtil.class);
        proxy.invoke(new Object());

        verifyStatic();
        ServiceLoggingUtil.captureClientDetailsAtStart(anyObject(), anyString(), anyObject());

        verifyStatic();
        ServiceLoggingUtil.captureClientDetailsAtEnd();

        verifyStatic();
        ServiceLoggingUtil.clearContext();

    }


}
