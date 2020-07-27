package com.pb.stratus.controller.model;

/**
 * Created by SU019CH on 8/9/2019.
 */
public class Option {

    private String name;
    private String value;

    public Option(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
