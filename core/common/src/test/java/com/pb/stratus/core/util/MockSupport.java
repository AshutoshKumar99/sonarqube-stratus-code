package com.pb.stratus.core.util;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

public class MockSupport
{

    private Collection<Object> mocks = new LinkedList<Object>();

    public HttpServletRequest createMockRequest()
    {
        return createMock(HttpServletRequest.class);
    }

    public HttpServletResponse createMockResponse()
    {
        return createMock(HttpServletResponse.class);
    }

    public <T> T createMock(Class<T> type)
    {
        T obj = EasyMock.createMock(type);
        mocks.add(obj);
        return obj;
    }

    public void verifyAllMocks()
    {
        for (Object o : mocks)
        {
            EasyMock.verify(o);
        }
    }

    public void replayAllMocks()
    {
        for (Object o : mocks)
        {
            EasyMock.replay(o);
        }
    }

}
