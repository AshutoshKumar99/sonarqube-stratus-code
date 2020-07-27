package com.pb.stratus.util.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import uk.co.graphdata.utilities.contract.Contract;

public class MockURLConnection extends URLConnection 
{
    
    private byte[] content;
    
    private String contentType;
    
    private String encoding;

    protected MockURLConnection(URL url, byte[] content, String contentType, 
            String encoding) 
    {
        super(url);
        Contract.pre(url != null, "URL required");
        Contract.pre(content != null, "Content required");
        Contract.pre(contentType != null, "Content type required");
        this.content = content;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    @Override
    public void connect() throws IOException 
    {
    }

    @Override
    public String getContentEncoding() 
    {
        return encoding;
    }

    @Override
    public String getContentType() 
    {
        return contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException 
    {
        return new ByteArrayInputStream(content);
    }
}
