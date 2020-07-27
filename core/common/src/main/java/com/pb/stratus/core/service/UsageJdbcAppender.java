package com.pb.stratus.core.service;

import com.pb.stratus.core.configuration.ConfigProperties;
import com.pb.stratus.core.configuration.db.DatabaseConnectionSource;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.apache.logging.log4j.core.config.Configuration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/31/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageJdbcAppender {
    private static final String APPENDER_NAME = "jdbc-appender";

    public static Appender getJdbcAppender(ConfigProperties configProperties, Configuration log4jConfig) {
        ConnectionSource connectionSource = new DatabaseConnectionSource(configProperties);
        JdbcAppender appender = JdbcAppender.createAppender(APPENDER_NAME, "true", null, connectionSource,
                configProperties.getUsageDBBufferSize(), configProperties.getUsageTableName(), createColumnConfigArray(log4jConfig));
        appender.start();
        log4jConfig.addAppender(appender);
        return appender;
    }

    private static ColumnConfig[] createColumnConfigArray(Configuration config) {
        //INSERT INTO `blue`.`connect_usage` (`tenant`, `year`, `month`, `day`, `minute`, `second`, `hour`, `hostip`, `params`,
        // `responsecode`, `responsetime`, `responsesize`) VALUES ('Barnsley', 2014, '1', '1', '1', '1', '1', '1', 'dsdsdsd','21', '2', '1');
        //"Application=%X{application}, RequestedTime=%d{yyyy-MMM-dd HH:mm:ss}, Host=%X{externalHostIp},
        // InternalHost=%X{remoteIpAddr}, UserAgent=%X{userAgent}, Tenant=%X{tenant}, Operation=%X{operationName}, MapConfig=%X{mapcfg}, BaseURL=%X{requestURL},
        // UrlParams=%X{urlParams}, User=%X{user}, SessionId=%X{sessionId}, ResponseStatus=%X{statusCode}, ResponseTime=%X{responseTime}, ResponseSize=%X{responseSize} %n";

        ColumnConfig tenantCol = ColumnConfig.createColumnConfig(config, "tenant", "%X{tenant}", null, null, null, null);
        ColumnConfig yearCol = ColumnConfig.createColumnConfig(config, "year", "%d{yyyy}", null, null, null, null);
        ColumnConfig monthCol = ColumnConfig.createColumnConfig(config, "month", "%d{MM}", null, null, null, null);
        ColumnConfig dayCol = ColumnConfig.createColumnConfig(config, "day", "%d{dd}", null, null, null, null);
        ColumnConfig minCol = ColumnConfig.createColumnConfig(config, "minute", "%d{mm}", null, null, null, null);
        ColumnConfig secondCol = ColumnConfig.createColumnConfig(config, "second", "%d{ss}", null, null, null, null);
        ColumnConfig hourCol = ColumnConfig.createColumnConfig(config, "hour", "%d{HH}", null, null, null, null);
        ColumnConfig hostIpCol = ColumnConfig.createColumnConfig(config, "hostip", "%X{externalHostIp}", null, null, null, null);
        ColumnConfig internalHostCol = ColumnConfig.createColumnConfig(config, "internalhostip", "%X{remoteIpAddr}", null, null, null, null);
        ColumnConfig userAgentCol = ColumnConfig.createColumnConfig(config, "useragent", "%X{user-agent}", null, null, null, null);
        ColumnConfig operationCol = ColumnConfig.createColumnConfig(config, "operation", "%X{operationName}", null, null, null, null);
        ColumnConfig configCol = ColumnConfig.createColumnConfig(config, "config", "%X{mapcfg}", null, null, null, null);
        ColumnConfig baseUrlCol = ColumnConfig.createColumnConfig(config, "baseurl", "%X{requestURL}", null, null, null, null);
        ColumnConfig paramsCol = ColumnConfig.createColumnConfig(config, "params", "%X{urlParams}", null, null, null, null);
        ColumnConfig userHandleCol = ColumnConfig.createColumnConfig(config, "userhandle", "%X{user}", null, null, null, null);
        ColumnConfig sessionIdCol = ColumnConfig.createColumnConfig(config, "sessionid", "%X{sessionId}", null, null, null, null);
        ColumnConfig responseCodeCol = ColumnConfig.createColumnConfig(config, "responsecode", "%X{statusCode}", null, null, null, null);
        ColumnConfig responseTimeCol = ColumnConfig.createColumnConfig(config, "responsetime", "%X{responseTime}", null, null, null, null);
        ColumnConfig responseSizeCol = ColumnConfig.createColumnConfig(config, "responsesize", "%X{responseSize}", null, null, null, null);
        ColumnConfig requestTimeStamp = ColumnConfig.createColumnConfig(config, "requesttimestamp", null, null, "true", null, null);
        return new ColumnConfig[]{tenantCol, yearCol, monthCol, dayCol, minCol, secondCol, hourCol, hostIpCol, internalHostCol, userAgentCol, operationCol,
                configCol, baseUrlCol, paramsCol, userHandleCol, sessionIdCol, responseCodeCol, responseTimeCol, responseSizeCol, requestTimeStamp};
    }   
}