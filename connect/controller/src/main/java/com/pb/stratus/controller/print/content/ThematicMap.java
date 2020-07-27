package com.pb.stratus.controller.print.content;

import com.mapinfo.midev.service.mapping.v1.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 10/28/14
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThematicMap extends RuntimeMap {

    public ThematicMap(String name,String opacity, Map mapObject) {
        this.name = name;
        this.mapObject = mapObject;
        this.opacity =  opacity;
    }
}
