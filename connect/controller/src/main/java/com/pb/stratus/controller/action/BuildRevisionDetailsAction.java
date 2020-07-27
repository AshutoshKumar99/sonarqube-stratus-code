package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: HA008SA
 * Date: 4/2/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuildRevisionDetailsAction extends DataInterchangeFormatControllerAction {

    private static final Logger logger = LogManager.getLogger(BuildRevisionDetailsAction.class);

    public BuildRevisionDetailsAction() {}

    /**
     * Constructs the object to be serialised into JSON/CSV and sent back to the
     * caller.
     *
     * @param request the request of the caller that the JSON response will be
     *                sent to.
     * @return an arbitrary Java object.
     * @throws javax.servlet.ServletException XXX remove
     * @throws java.io.IOException            XXX remove
     */
    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        return getBuildRevisionDetails(request);
    }

    private String getBuildRevisionDetails(HttpServletRequest request) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("buildNumber.properties");
            properties.load(inputStream);
            //return "Analyst " + properties.getProperty("projectVersion") + " (rev. " + properties.getProperty("buildNumber") + ")";
            return properties.getProperty("ProductName") + " " + properties.getProperty("projectVersion") + " (rev. " + properties.getProperty("buildNumber") + ")";
        } catch (Exception e) {
            logger.warn("File buildNumber.properties could not be loaded. Version Number and Build Number will not be shown on Connect Application.");
        }
        return "";
    }
}
