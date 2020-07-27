package com.pb.stratus.controller.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 1/23/14
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionMonitor{

    public static final String MAPPING_SERVICE_LOGGER = "MappingTransactionMonitor";
    public static final String FEATURE_SERVICE_LOGGER = "FeatureTransactionMonitor";
    public static final String GEOMETRY_SERVICE_LOGGER = "GeometryTransactionMonitor";

    private Logger serviceLogger;

    public TransactionMonitor(String loggerName){

        this.serviceLogger = LogManager.getLogger(loggerName);
    }

    public void logTransaction(){
        if(serviceLogger != null)
            serviceLogger.info("");
    }

}
