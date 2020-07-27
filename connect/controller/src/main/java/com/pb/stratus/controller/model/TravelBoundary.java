package com.pb.stratus.controller.model;

/**
 * Created by SU019CH on 8/9/2019.
 */
public class TravelBoundary {

    private String unit;
    private String cost;

    public TravelBoundary(String unit, String cost) {
        this.unit = unit;
        this.cost = cost;
    }

    public String getUnit() {
        return unit;
    }

    public String getCost() {
        return cost;
    }
}
