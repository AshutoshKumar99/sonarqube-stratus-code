package com.pb.stratus.controller.service;

import com.pb.stratus.controller.info.FeatureSearchResult;

public class FeatureSearchOutcome<T>
{
    
    private T request;
    
    private FeatureSearchResult result;
    
    public FeatureSearchOutcome(T request, FeatureSearchResult result)
    {
        this.request = request;
        this.result = result;
    }
    
    
    public T getRequest()
    {
        return request;
    }
    
    public FeatureSearchResult getResult()
    {
        return result;
    }

}
