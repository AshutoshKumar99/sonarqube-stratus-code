package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by ar009sh on 13-07-2015.
 * checks whether env is analyst or saas and provides Spectrum/spatial server host port info .
 * If no profile is set ,saas profile gets loaded
 */
public class DeploymentIdentificationAction extends DataInterchangeFormatControllerAction {

    public final static String DEFAULT = "saas";
    public final static String PROFILE = "profile";


    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        String env = System.getProperty(PROFILE);
        JSONObject object = new JSONObject();
        object.put(PROFILE, StringUtils.isEmpty(env) ? DEFAULT : env);
        return object;
    }

}
