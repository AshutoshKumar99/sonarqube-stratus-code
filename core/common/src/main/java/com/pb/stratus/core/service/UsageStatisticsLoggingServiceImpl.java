package com.pb.stratus.core.service;

import com.pb.stratus.core.configuration.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/21/14
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageStatisticsLoggingServiceImpl implements UsageStatisticsLoggingService {
    private String USAGE_STATISTICS_LOGGER_NAME = "UsageStatisticsLogger";
    private static boolean appenderAttached;

    @Override
    public Logger getLogger() {
        return LogManager.getLogger(USAGE_STATISTICS_LOGGER_NAME);
    }

    @Override
    public void log(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Configuration getConfiguration(){
        org.apache.logging.log4j.core.Logger coreLogger
                = (org.apache.logging.log4j.core.Logger)getLogger();
        return coreLogger.getContext().getConfiguration();

    }

    @Override
    public Logger configureLogger(ConfigProperties configProperties) {
        if(!appenderAttached){
            configureAppender(configProperties);
            appenderAttached = true;
        }
        return getLogger();
    }

    /**
     * This method configure required appender to 'UsageStatisticsLogger' logger
     */
    private void configureAppender(ConfigProperties configProperties){
        UsageStatisticsConfiguration config = new UsageStatisticsConfigurationImpl(new UsageLoggingAppenderFactory(), this);
        if(configProperties != null){
            config.configureAppenders(configProperties);
        }else{
            throw new IllegalStateException("Config properties cannot be null to configure appender.");
        }

    }

    /**
     * method added for unit test
     * @param bool
     */
    public void appenderAttached(boolean bool){
        appenderAttached = bool;
    }

}
