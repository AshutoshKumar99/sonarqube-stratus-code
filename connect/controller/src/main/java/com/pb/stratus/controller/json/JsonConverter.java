package com.pb.stratus.controller.json;

public interface JsonConverter
{
    
    /**
     * Converts a JavaBean into a JSON string
     * 
     * @param o a JavaBean
     * @return a JSON String
     */
    public String toJson(Object o);

}
