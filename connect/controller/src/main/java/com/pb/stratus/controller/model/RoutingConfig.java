package com.pb.stratus.controller.model;

import com.pb.stratus.controller.AuthType;
import com.pb.stratus.controller.DeploymentType;

import java.util.List;

/**
 * Created by SU019CH on 8/9/2019.
 */
public class RoutingConfig {
    private String name;
    private String url;
    private AuthType authType;
    private String user;
    private String password;
    private DeploymentType deployment;
    private boolean isHistoricTrafficTimeBucket;
    private String defaultDatabase;
    private List<RoutingDB> databases;
    private TravelBoundary defaultTravelDistanceBoundary;
    private TravelBoundary defaultTravelTimeBoundary;
    private List<Option> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DeploymentType getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentType deployment) {
        this.deployment = deployment;
    }

    public boolean isHistoricTrafficTimeBucket() {
        return isHistoricTrafficTimeBucket;
    }

    public void setIsHistoricTrafficTimeBucket(boolean isHistoricTrafficTimeBucket) {
        this.isHistoricTrafficTimeBucket = isHistoricTrafficTimeBucket;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    public List<RoutingDB> getDatabases() {
        return databases;
    }

    public void setDatabases(List<RoutingDB> databases) {
        this.databases = databases;
    }

    public TravelBoundary getDefaultTravelDistanceBoundary() {
        return defaultTravelDistanceBoundary;
    }

    public void setDefaultTravelDistanceBoundary(TravelBoundary defaultTravelDistanceBoundary) {
        this.defaultTravelDistanceBoundary = defaultTravelDistanceBoundary;
    }

    public TravelBoundary getDefaultTravelTimeBoundary() {
        return defaultTravelTimeBoundary;
    }

    public void setDefaultTravelTimeBoundary(TravelBoundary defaultTravelTimeBoundary) {
        this.defaultTravelTimeBoundary = defaultTravelTimeBoundary;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
