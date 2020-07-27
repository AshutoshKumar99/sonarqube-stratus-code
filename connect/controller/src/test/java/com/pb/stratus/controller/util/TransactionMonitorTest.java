package com.pb.stratus.controller.util;


import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionMonitorTest
{

    private TransactionMonitor target;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {}

    //@Test
    public void testLogTransactionForRequiredService() throws Exception
    {
        Appender serviceAppender = mock(Appender.class);
        //LogManager.getLogger("XYZService").addAppender(serviceAppender);

        target = new TransactionMonitor("XYZService");
        target.logTransaction();

        //verify(serviceAppender).doAppend(any(LoggingEvent.class));
    }

}
