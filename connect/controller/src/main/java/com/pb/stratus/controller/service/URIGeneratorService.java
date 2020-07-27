package com.pb.stratus.controller.service;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.TenantNameHolder;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by GU003DU on 15-Nov-18.
 */
public class URIGeneratorService {

    private TenantNameHolder tenantNameHolder;
    private ControllerConfiguration controllerConfiguration;

    public URIGeneratorService(TenantNameHolder tenantNameHolder, ControllerConfiguration controllerConfiguration) {
        this.tenantNameHolder = tenantNameHolder;
        this.controllerConfiguration = controllerConfiguration;
    }

    public URI getTileServiceProfileURI(String tileServiceProfile) throws Exception {
        StringBuilder path = new StringBuilder(controllerConfiguration.getSSAMapProjectBaseUrl().toString());
        if (!controllerConfiguration.getSSAMapProjectBaseUrl().toString().endsWith("/")){
            path.append("/");
        }
        path.append(tenantNameHolder.getTenantName());
        path.append("/map-project/tileServiceProfiles/");
        path.append(tileServiceProfile);

        return new URI(path.toString());
    }
}
