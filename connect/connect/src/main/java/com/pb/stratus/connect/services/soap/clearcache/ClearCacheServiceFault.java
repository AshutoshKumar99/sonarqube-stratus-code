package com.pb.stratus.connect.services.soap.clearcache;

import javax.xml.ws.WebFault;

@WebFault(name = "ClearCacheServiceFault", targetNamespace = "http://www.pb.com/connect/services/soap/clearcacheservice/v1")
public class ClearCacheServiceFault extends RuntimeException {

    public ClearCacheServiceFault()
    {
        super();
    }
    public ClearCacheServiceFault(String message)
    {
        super(message);
    }
    public ClearCacheServiceFault(Throwable throwable)
    {
        super(throwable);
    }
}
