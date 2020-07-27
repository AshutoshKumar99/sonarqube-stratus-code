package com.pb.stratus.core.service;


import com.pb.stratus.core.configuration.ConfigProperties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 24/07/14
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */


public class UsageCsvAppenderTest {

    private UsageCsvAppender target;
    private Configuration mockConfiguration;

    private ConfigProperties mockProperties;
    private org.apache.logging.log4j.core.Logger coreLogger;
    public static String mockLOG_FILE_NAME_PATTERN = "${date:yyyy-MM-ss}" + File.separatorChar + "ConnectUsage-%d{MM-dd-yyyy-HH-mm-ss}.csv";
    public static final String TEST_APPENDER_PATH = "src" + File.separatorChar + "test" + File.separatorChar + "resources";
    public static final String LOG_FILE_NAME = "ConnectUsage";

    @Before
    public void setup() {
        mockProperties = mock(ConfigProperties.class);

        mockConfiguration = new DefaultConfiguration();
        target = new UsageCsvAppender();

    }

    @After
    public void tearDown() throws IOException {
        FileUtils.cleanDirectory(new File(TEST_APPENDER_PATH));

    }


    @Test
    public void testCSVAppenderWhenPathToCSVisEmpty() {
        Mockito.when(mockProperties.getUsageDataSourceFile()).thenReturn("");


        Appender appender = target.createRollingFileAppender(mockProperties, mockConfiguration);
        assertNull(appender);
        System.out.println("Test1 Passed");
    }

    @Test
    public void testCSVAppenderWhenPathToCSVisNull() {
        Mockito.when(mockProperties.getUsageDataSourceFile()).thenReturn(null);

        Appender appender = target.createRollingFileAppender(mockProperties, mockConfiguration);

        assertNull(appender);
        System.out.println("Test2 Passed");

    }

    @Test
    public void testCSVAppenderWhenPathToCSVisValid() {
        Mockito.when(mockProperties.getUsageDataSourceFile()).thenReturn(TEST_APPENDER_PATH);
        Mockito.when(mockProperties.getUsageCSVFileName()).thenReturn(LOG_FILE_NAME);


        Appender appender = target.createRollingFileAppender(mockProperties, mockConfiguration);
        File resourcesFolder = new File(TEST_APPENDER_PATH);
        Boolean activeLogFileFound = false;
        for (File monthlyFolder : resourcesFolder.listFiles()) {
            if (monthlyFolder.getName().equalsIgnoreCase(LOG_FILE_NAME + ".csv")) {
                activeLogFileFound = true;
                break;
            }
        }
        assertTrue(activeLogFileFound);
        appender.stop();
        System.out.println("Test3 Passed");


    }

   /*@Ignore
    public void testCSVAppenderWithRolling () throws Exception {
        Mockito.when(mockProperties.getUsageDataSourceFile()).thenReturn(TEST_APPENDER_PATH);
       Mockito.when(mockProperties.getUsageCSVFileName()).thenReturn(LOG_FILE_NAME);


        Whitebox.setInternalState(target,"LOG_FILE_NAME_PATTERN",mockLOG_FILE_NAME_PATTERN);

        Appender appender = target.createRollingFileAppender(mockProperties,mockConfiguration);
        Logger logger1 = LogManager.getRootLogger();
        coreLogger= (org.apache.logging.log4j.core.Logger)logger1;
        coreLogger.addAppender(appender);
        coreLogger.info("appender1");
        coreLogger.error("appender2");
        Thread.sleep(1000);
        coreLogger.error("appender3");
        Thread.sleep(1000);

        coreLogger.error("appender4");
        File resourcesFolder = new File(TEST_APPENDER_PATH) ;


        Boolean rollingSuccessfull=false;
        if(resourcesFolder.listFiles().length>1)
        {rollingSuccessfull=true;
        }
        assertTrue(rollingSuccessfull);
        appender.stop();
       System.out.println("Test4 Passed");


    }*/

}
