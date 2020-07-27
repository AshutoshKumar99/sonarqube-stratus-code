package com.pb.stratus.util.test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler 
{
    
    private static ThreadLocal<Object[]> threadLocalParams = 
        new ThreadLocal<Object[]>();
    
    private static ThreadLocal<MockURLConnection> threadLocalConnections = 
        new ThreadLocal<MockURLConnection>();
    
    public static void setCurrentURLConnection(byte[] content, 
            String contentType, String encoding)
    {
        threadLocalParams.set(new Object[] {content, contentType, encoding});
    }
    
    public static MockURLConnection getLastOpenedURLConnection()
    {
        return threadLocalConnections.get();
    }
    
    public static void release()
    {
        threadLocalParams.set(null);
        threadLocalConnections.set(null);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException 
    {
        Object[] params = threadLocalParams.get();
        MockURLConnection conn = new MockURLConnection(u, (byte[]) params[0], 
                (String) params[1], (String) params[2]);
        threadLocalConnections.set(conn);
        return conn;
    }

}
