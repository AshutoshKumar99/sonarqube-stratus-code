package com.pb.stratus.controller.action;

import com.pb.stratus.security.core.util.AuthorizationUtils;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * This class is used to get the list of authorized map configs
 */
public class GetAuthorizedMapConfigAction extends
        DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager.getLogger(GetAuthorizedMapConfigAction.class);
    
    public static final String ROLE_PUBLIC = "ROLE_Public";

    private AuthorizationUtils authorizationUtils;

    private boolean forAuthorizedProjects;

    public GetAuthorizedMapConfigAction(AuthorizationUtils authorizationUtils, boolean forAuthorizedProjects) {
        this.authorizationUtils = authorizationUtils;
        this.forAuthorizedProjects = forAuthorizedProjects;
    }

    public GetAuthorizedMapConfigAction(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
        this.forAuthorizedProjects = false;
    }
    
    @Override
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException
    {
        Set<ResourceAuthorizationConfig> configs = null;
        try
        {
            configs = authorizationUtils.getAuthorizeConfigs(request, ResourceType.MAP_CONFIG);

        }
        catch (ResourceException e1)
        {
            logger.debug(e1.getMessage(), e1);
        }
        if(forAuthorizedProjects) {
            return getAuthorizedProjects(configs);
        }
        List<String> mapConfigs = new ArrayList<String>();

        if(configs != null){
            for(ResourceAuthorizationConfig config : configs){
                mapConfigs.add(config.getName());
            }
        }
        if (mapConfigs.size() > 0)
        {
            //sort mapConfigs alphabetically irrespective of the case
            Collections.sort(mapConfigs, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }
        return mapConfigs;
    }

    private Object getAuthorizedProjects(Set<ResourceAuthorizationConfig> configs) {

        List<MapProject> mapProjects = new ArrayList<>();
        if(configs != null){
            for(ResourceAuthorizationConfig config : configs){
                mapProjects.add(new MapProject(config.getName(),config.getGrantedAuthorities().size()));
            }
        }
        if (mapProjects.size() > 0)
        {
            //sort mapConfigs alphabetically irrespective of the case
            Collections.sort(mapProjects, new Comparator<MapProject>() {
                public int compare(MapProject s1, MapProject s2) {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }
            });
        }
        return mapProjects;

    }



    public static class MapProject {
        private String name;
        private int numberOfRoles;

        public MapProject(String name, int numberOfRoles) {
            this.name = name;
            this.numberOfRoles = numberOfRoles;
        }

        public String getName() {
            return name;
        }

        public int getNumberOfRoles() {
            return numberOfRoles;
        }
    }
}
