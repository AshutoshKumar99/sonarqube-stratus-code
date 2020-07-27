package com.pb.stratus.core.configuration;

import com.pb.stratus.core.service.UsageCsvAppender;
import com.pb.stratus.core.service.UsageJdbcAppender;
import com.pb.stratus.core.service.UsageStatisticsLoggingService;
import com.pb.stratus.core.service.UsageStatisticsLoggingServiceImpl;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/21/14
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageLoggingAppenderFactory {
     /**
     * This is a factory method that returns runtime instance of Appender based  on the
     * usageDataSource.
     *
      * @param dataSource
      * @param configProperties
      * @return
     */
    Appender createAppender(UsageDataSourceEnum dataSource, ConfigProperties configProperties){

        UsageStatisticsLoggingService usageStatisticsLoggingService = new UsageStatisticsLoggingServiceImpl();
        Configuration log4jConfig = usageStatisticsLoggingService.getConfiguration();
        if (dataSource.equals(UsageDataSourceEnum.CSV)){

            return UsageCsvAppender.createRollingFileAppender(configProperties, log4jConfig);

        }
        else if(dataSource.equals(UsageDataSourceEnum.MYSQL))
        {
             return UsageJdbcAppender.getJdbcAppender(configProperties, log4jConfig);
        }

        // returns no appender
         return null;
    }


}
