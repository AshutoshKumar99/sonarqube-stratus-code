package com.pb.stratus.controller.routing;

import java.util.List;
import java.util.Map;

/**
 * Created by NI010GO on 3/7/2016.
 */
public class RoutingDbConfig {
    private String url;
    private String name;
    private String user;
    private String password;
    private String deployment;
    private List<String> databases;
    private HistoricDB historicDB;
    private Map<String, String> options;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDeployment() {
        return deployment;
    }

    public void setDeployment(String deployment) {
        this.deployment = deployment;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public HistoricDB getHistoricDB() {
        return historicDB;
    }

    public void setHistoricDB(HistoricDB historicDB) {
        this.historicDB = historicDB;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getUrl() {

        return url;
    }
}
