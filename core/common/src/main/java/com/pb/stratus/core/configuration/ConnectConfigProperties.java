package com.pb.stratus.core.configuration;

import com.pb.stratus.web.service.CaptureUsageStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/14
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectConfigProperties implements ConfigProperties {

    private static final Logger logger= LogManager.getLogger(ConnectConfigProperties.class);
    private final String USAGE_CSV_FILE_NAME = "ConnectUsage";
    private final String USAGE_TABLE_NAME = "connectusage";

    private final String PATTERN = CaptureUsageStatistics.PATTERN;

    private  ControllerConfiguration config = null;

    public ConnectConfigProperties(){
        //reload();
    }

    @Override
    public void reload() {

        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.
                    currentRequestAttributes()).getRequest();
            config = TenantConfiguration.getTenantConfiguration(request);
        }catch (IllegalStateException ex){
            logger.error(ex);
            throw ex;
        }
    }

    @Override
    public String getUsageDataSource() {
        reload();
        return config.getUsageDataSource();
    }

    @Override
    public String getUsageDataSourceFile() {
        reload();
        return config.getUsageDataSourceFile();
    }

    @Override
    public String getUsageDBUserName() {
        reload();
        return config.getDBUserName();
    }

    @Override
    public String getUsageDBDriverClassName() {
        reload();
        return config.getDBDriverClassName();
    }

    @Override
    public String getUsageDBUrl() {
        reload();
        return config.getDBUrl();
    }

    @Override
    public String getUsageDBPassword() {
        reload();
        return config.getDBPassword();
    }

    @Override
    public String getUsageDBConnectionPoolSize() {
        reload();
        return config.getPoolSize();
    }

    @Override
    public String getUsageDBBufferSize() {
        reload();
        return config.getDBBufferSize();
    }

    @Override
    public String getUsageTableName() {
        return USAGE_TABLE_NAME;
    }

    @Override
    public String getUsageCSVFileName() {
        return USAGE_CSV_FILE_NAME;
    }

    @Override
    public String getCsvAppenderPattern() {
        return PATTERN;
    }

    @Override
    public boolean isCaptureUsageDataEnabled() {
        reload();
        return config.isCaptureUsageData();
    }


}
