package com.pb.stratus.connect.services.rest;

import net.sf.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/12
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocatorJsonObject {
    private final JSONObject json;

    public LocatorJsonObject(JSONObject json){
        this.json = json;
    }

    public JSONObject getJSON(){
        return json;
    }
}
