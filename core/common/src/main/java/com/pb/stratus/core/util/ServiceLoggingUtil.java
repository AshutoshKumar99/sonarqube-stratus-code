/**
 * A helper class for facilitating  the the tracking & logging of times taken at various points  in a service request.
 * User: GU003DU
 * Date: 9/27/13
 * Time: 2:52 PM
 *
 */

package com.pb.stratus.core.util;

import com.mapinfo.midev.service.feature.v1.*;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.ThreadContext;


import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ServiceLoggingUtil {

    // Constants
    public static final String REQUEST_ID = "req.id";
    public static final String SS_OP = "ss.op";
    public static final String STS_START_T = "sts.st.t";
    public static final String STS_T = "sts.t";
    public static final String SS_START_T = "ss.st.t";
    public static final String SS_T = "ss.t";
    public static final String SS_INVOKE_START_T = "invoke.st.t";
    public static final String SS_INVOKE_T = "invoke.t";
    // only support names of resources for mapping service operations.
    public static final String SS_OP_ARGS = "ss.op.args";

    // maximum time that is a client is expected to take to fulfill the request.
    private static final int MAX_TIME_LIMIT = 5000;

    private static final String RESOURCE_SEPARATOR = ",";

    // Messages
    public static final String WARNING_MAX_TIME_EXCEEDED = " invocation took more than " + (MAX_TIME_LIMIT / 1000) + " seconds.";

    private static final Logger logger = LogManager.getLogger(ServiceLoggingUtil.class);

    /**
     * @param startTimeKey time value key
     * @return  total time elapsed
     */
    public static Long getTotalTime(String startTimeKey) {

        Long startTime =0l;

        try{
            startTime = Long.parseLong(ThreadContext.get(startTimeKey));
        }catch(NumberFormatException ex)
        {
            logger.error("startTimeKey not a long value");
        }

        return System.currentTimeMillis() - startTime;

    }

    public static void captureClientDetailsAtStart(Object target, String opName, Object arguments) {
        ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
        ThreadContext.put(SS_INVOKE_START_T, ""+System.currentTimeMillis());
        ThreadContext.put(SS_OP, opName);
        captureResourcesForService(target, arguments);
    }

    public static void captureClientDetailsAtEnd() {
        ThreadContext.put(SS_INVOKE_T, ""+getTotalTime(SS_INVOKE_START_T));
        if (logger.isDebugEnabled()) {
            logger.debug("Request processed at client");
        }
    }

    public static void captureSTSStartTime() {
        ThreadContext.put(STS_START_T,""+System.currentTimeMillis());
    }

    public static void captureSTSEndTime() {
        ThreadContext.put(STS_T, ""+getTotalTime(STS_START_T));
    }

    public static void captureSSStartTime() {
        ThreadContext.put(SS_START_T,""+ System.currentTimeMillis());
    }

    public static void captureSSEndTime() {
        ThreadContext.put(SS_T,""+ getTotalTime(SS_START_T));

        // Set the sts time to 0 explicitly,if current thread context doesn't have it; this is to avoid the appender to
        // use the value from MDC copy.
        if (ThreadContext.get(STS_T) == null) {
            ThreadContext.put(STS_T, ""+0);
        }
    }

    public static void clearContext(){
        ThreadContext.remove(REQUEST_ID);
        ThreadContext.remove(SS_INVOKE_START_T);
        ThreadContext.remove(SS_OP);
        ThreadContext.remove(SS_START_T);
        ThreadContext.remove(STS_START_T);
        ThreadContext.remove(SS_T);
        ThreadContext.remove(STS_T);
        ThreadContext.remove(SS_INVOKE_T);
        ThreadContext.remove(SS_OP_ARGS);

    }

    public static void captureResourcesForMappingService(Object argument){
        String resources = "";
        if(argument instanceof RenderNamedMapRequest){
            resources = ((RenderNamedMapRequest) argument).getNamedMap();
        }else if(argument instanceof RenderMapRequest){
            List<Layer> layers = ((RenderMapRequest) argument).getMap().getLayer();
            Iterator<Layer> it = layers.iterator();
            while(it.hasNext()){
                Layer layer = it.next();
                if(layer instanceof NamedLayer){
                    String resource = resources != ""? (RESOURCE_SEPARATOR + ((NamedLayer) layer).getName()):((NamedLayer) layer).getName();
                    resources = resources + resource;
                }
            }

        }else if(argument instanceof DescribeNamedLayerRequest){
            resources = ((DescribeNamedLayerRequest) argument).getNamedLayer();
        }else if(argument instanceof DescribeNamedMapRequest){
            resources = ((DescribeNamedMapRequest) argument).getNamedMap();
        }else if(argument instanceof GetNamedMapLegendsRequest){
            resources = ((GetNamedMapLegendsRequest) argument).getNamedMap();
        }
        ThreadContext.put(SS_OP_ARGS, resources);
    }

    public static void captureResourcesForService(Object target, Object request) {
        if(target instanceof MappingServiceInterface){
            captureResourcesForMappingService(request);
        }else if(target instanceof FeatureServiceInterface){
            captureResourcesForFeatureService(request);
        }
    }

    public static void captureResourcesForFeatureService(Object argument) {
        String resources = "";
        if(argument instanceof DescribeTableRequest){
            resources = ((DescribeTableRequest) argument).getTable();
        }else if(argument instanceof SearchNearestRequest){
            resources = ((SearchNearestRequest) argument).getTable().getName();
        }else if(argument instanceof SearchIntersectsRequest){
            resources = ((SearchIntersectsRequest) argument).getTable().getName();
        }else if(argument instanceof SearchAtPointRequest){
            resources = ((SearchAtPointRequest) argument).getTable().getName();
        }else if(argument instanceof SearchBySQLRequest){
           // resources = ((SearchBySQLRequest) argument).getTable().getName();
        }
        ThreadContext.put(SS_OP_ARGS, resources);
    }
}
