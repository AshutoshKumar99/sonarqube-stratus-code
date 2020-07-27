package com.pb.stratus.core.configuration;

import com.pb.stratus.core.service.UsageStatisticsLoggingService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/21/14
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageStatisticsConfigurationImpl implements UsageStatisticsConfiguration {


    private UsageLoggingAppenderFactory appenderFactory;
    private UsageStatisticsLoggingService loggingService;

    public  UsageStatisticsConfigurationImpl(UsageLoggingAppenderFactory factory, UsageStatisticsLoggingService loggingService){
        this.appenderFactory = factory;
        this.loggingService = loggingService;
    }

    /**
     * Reads property(usage.data.source) to choose appender to configure
     * configures appender accordingly.
     * @param configProperties
     */
    @Override
    public void configureAppenders(ConfigProperties configProperties) {
        String usageDataSourceStr = configProperties.getUsageDataSource();
        UsageDataSourceEnum dataSource = UsageDataSourceEnum.getDataSource(usageDataSourceStr);
        Appender appender = appenderFactory.createAppender(dataSource, configProperties);
        if(appender!=null){
            addLoggerAppender(loggingService.getLogger(), appender);
        }
    }

    private void addLoggerAppender(Logger logger, Appender appender) {
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger)logger;
        coreLogger.addAppender(appender);
    }
}
