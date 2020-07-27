package com.pb.stratus.controller.model;

/**
 * Created by SU019CH on 8/9/2019.
 */
public class RoutingDB {

    private String name;
    private boolean enable;

    public RoutingDB(String name, boolean enable) {
        this.name = name;
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public boolean isEnable() {
        return enable;
    }
}
