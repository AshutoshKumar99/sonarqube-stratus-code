package com.pb.stratus.controller.geocoder;

import com.pb.stratus.controller.AuthType;
import com.pb.stratus.controller.DeploymentType;
import com.pb.stratus.controller.GeocodingServiceType;
import com.pb.stratus.controller.model.Option;

import java.util.List;

/**
 * Created by ar009sh on 07-09-2015.
 */
public class GeoSearchParams {

    private String username;
    private String password;
    private AuthType authType;
    private DeploymentType deploymentType;
    private GeocodingServiceType serviceType;
    private String name;
    private String endPoint;
    private String country;
    private List<Option> options;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GeocodingServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(GeocodingServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public DeploymentType getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(DeploymentType deploymentType) {
        this.deploymentType = deploymentType;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
