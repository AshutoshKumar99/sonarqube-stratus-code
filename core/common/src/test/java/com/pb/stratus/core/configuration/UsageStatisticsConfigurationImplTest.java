package com.pb.stratus.core.configuration;

import com.pb.stratus.core.service.UsageStatisticsLoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/21/14
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class UsageStatisticsConfigurationImplTest {

    private UsageStatisticsConfigurationImpl target;
    private UsageLoggingAppenderFactory mockFactory;
    private UsageStatisticsLoggingService mockService;
    private ControllerConfiguration mockConfiguration;
    private String USAGE_DATA_SOURCE = "xyz";

    @Before
    public void setup(){
        mockFactory = mock(UsageLoggingAppenderFactory.class);
        mockService = mock(UsageStatisticsLoggingService.class);
        mockConfiguration = mock(ControllerConfiguration.class);

        target = new UsageStatisticsConfigurationImpl(mockFactory, mockService);
    }

    @Test
    public void configureAppendersTest() throws IOException {
        ConfigProperties mockConfigProperties = mock(ConfigProperties.class);
        when(mockConfigProperties.getUsageDataSource()).thenReturn(USAGE_DATA_SOURCE);
        when(mockConfiguration.getUsageDataSource()).thenReturn(USAGE_DATA_SOURCE);
        Logger mockLogger = LogManager.getRootLogger();
        Logger spy = spy(mockLogger);
        when(mockService.getLogger()).thenReturn(spy);
        Appender mockAppender = new DefaultConfiguration().getAppender("Console");
        when(mockFactory.createAppender(any(UsageDataSourceEnum.class), any(ConfigProperties.class))).thenReturn(mockAppender);
        target.configureAppenders(mockConfigProperties);

        verify(mockFactory).createAppender(UsageDataSourceEnum.NONE, mockConfigProperties);
        verify(mockService).getLogger();
        verify((org.apache.logging.log4j.core.Logger)spy).addAppender(any(Appender.class));
    }

    @Test
    public void configureAppendersWithNullAppenderTest() throws IOException {
        String str = new String("csv");
        ConfigProperties mockConfigProperties = mock(ConfigProperties.class);
        when(mockConfiguration.getUsageDataSource()).thenReturn("none");
        Logger mockLogger = LogManager.getRootLogger();
        Logger spy = spy(mockLogger);
        when(mockService.getLogger()).thenReturn(spy);
        when(mockFactory.createAppender(any(UsageDataSourceEnum.class), any(ConfigProperties.class))).thenReturn(null);
        target.configureAppenders(mockConfigProperties);

        verify(mockFactory).createAppender(UsageDataSourceEnum.NONE, mockConfigProperties);
        verify(mockService, never()).getLogger();
        verify((org.apache.logging.log4j.core.Logger)spy, never()).addAppender(any(Appender.class));
    }

}
