package com.pb.stratus.core.configuration.db;

import com.pb.stratus.core.configuration.ConfigProperties;
import com.pb.stratus.core.service.UsageCsvAppender;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.pb.stratus.core.configuration.db.DatabaseConnectionFactory;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/29/14
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseConnectionFactoryTest {
    private ConfigProperties mockConfigProperties;
    private static final String USAGE_DB_URL = "jdbc:mysql://UDBAnalytics01:3306/blue";
    private static final String USAGE_USERNAME = "blue";
    private static final String USAGE_PASSWORD = "blue";
    private ConnectionFactory connectionFactory = null;

    @Before
    public void setup(){
        mockConfigProperties = mock(ConfigProperties.class);
        ConfigProperties mockConfigProperties = mock(ConfigProperties.class);
        when(mockConfigProperties.getUsageDBUrl()).thenReturn(USAGE_DB_URL);
        when(mockConfigProperties.getUsageDBUserName()).thenReturn(USAGE_USERNAME);
        when(mockConfigProperties.getUsageDBPassword()).thenReturn(USAGE_PASSWORD);
    }

    @After
    public void tearDown() throws IOException {

    }

    @Test
    public void testCreateDbConnectionFactory() {

        if(null != mockConfigProperties)
            connectionFactory = DatabaseConnectionFactory.createFactory(mockConfigProperties);
        else {
            throw new IllegalStateException("Config Properties cannot be null.");
        }
        assertNotNull(connectionFactory);
    }
}
