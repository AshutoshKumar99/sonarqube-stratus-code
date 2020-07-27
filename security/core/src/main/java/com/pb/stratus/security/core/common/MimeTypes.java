package com.pb.stratus.security.core.common;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/12
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MimeTypes {
    JSON("application/json"),
    XML("application/xml"),
    GEOJSON("application/geojson");

    private final String value;

    MimeTypes(String value){
        this.value = value;
    }

    public static MimeTypes fromValue(String value){
         for(MimeTypes type: MimeTypes.values()){
             if(type.value.equals(value)){
                 return  type;
             }
         }
        throw new IllegalArgumentException(value);
    }
}
