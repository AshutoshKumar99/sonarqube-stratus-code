package com.pb.stratus.core.service;

import com.pb.stratus.core.configuration.ConfigProperties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/21/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UsageStatisticsLoggingService {

    Logger getLogger();

    void log(String message);

    Configuration getConfiguration();

    Logger configureLogger(ConfigProperties configProperties);

}
