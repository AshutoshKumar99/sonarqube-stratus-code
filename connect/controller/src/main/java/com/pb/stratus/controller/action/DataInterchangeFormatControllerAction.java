package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.datainterchangeformat.DataInterchangeFormatFactory;
import com.pb.stratus.controller.datainterchangeformat.DataInterchangeFormatResponse;
import com.pb.stratus.controller.datainterchangeformat.ExtractFormatFromRequest;
import com.pb.stratus.controller.infrastructure.RequestCookieHandler;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is the bifurcating class for sending the response in various
 * formats like csv, json etc.(currently csv and json are the only formats
 * supported.
 * The client will send a parameter named "format" as a request asking fro
 * the type of response it wants. If no format is specified the by default it
 * will be treated as a request for JSon response.
 */
public abstract class DataInterchangeFormatControllerAction
        extends BaseControllerAction {

    public static final String DEFAULT_INTERCHANGE_FORMAT = "json";
    private String format;
    private RequestCookieHandler requestCookieHandler;
    private ExtractFormatFromRequest extractFormatFromRequest =
            new ExtractFormatFromRequest();
    
    /**
     * Converts the given result object into a UTF-8 encoded JSON string and 
     * sends it back via the provided response object.
     * 
     * @param response the HTTP servlet response to send the JSON string back
     *        to the caller.
     * @param results an arbitrary Java object to be serialised into JSON.
     * @throws IOException if the response could not be written
     */
    private void sendResponse(HttpServletResponse response, Object results) 
            throws IOException
    {
        DataInterchangeFormatResponse dataInterchangeFormatResponse =
                DataInterchangeFormatFactory.getDataInterchangeFormatResponse(
                        format);
        if(requestCookieHandler != null && requestCookieHandler.
                isCookieRequestValid())
        {
            dataInterchangeFormatResponse.addCookieToResponse(response,
                    requestCookieHandler.getCookie());
        }
        dataInterchangeFormatResponse.send(response, results);
    }

    public void execute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            InvalidGazetteerException {
        setCookieRequest(request);
        setInterchangeFormat(request);
        Object o = createObject(request);
        sendResponse(response, o);
    }

    private void setInterchangeFormat(HttpServletRequest request)
    {
        format = extractFormatFromRequest.extract(request);
        if(StringUtils.isBlank(format))
        {
            format = DEFAULT_INTERCHANGE_FORMAT;
        }
    }
    
    private void setCookieRequest(HttpServletRequest request)
    {
        requestCookieHandler = new RequestCookieHandler(request);
    }
    
    /**
     * Constructs the object to be serialised into JSON/CSV and sent back to the
     * caller.
     * 
     * @param request the request of the caller that the JSON response will be
     *        sent to. 
     * @return an arbitrary Java object.
     * @throws ServletException XXX remove
     * @throws IOException XXX remove
     */
    protected abstract Object createObject(HttpServletRequest request)
            throws ServletException, IOException, InvalidGazetteerException;

}
