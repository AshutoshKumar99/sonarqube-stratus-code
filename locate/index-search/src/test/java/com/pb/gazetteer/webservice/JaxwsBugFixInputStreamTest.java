package com.pb.gazetteer.webservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JaxwsBugFixInputStreamTest
{
    @Mock
    private InputStream mockStream;


    @Test
    public void testReadsEndOnlyOnce() throws IOException
    {
        when(mockStream.read())
                .thenReturn(-1)
                .thenThrow(new IllegalStateException());

        JaxwsBugFixInputStream fixedStream = new JaxwsBugFixInputStream(mockStream);


        //multiple calls to read should continue to produce -1
        //instead of underlying IllegalStateException (bug being worked around)
        assertEquals(-1, fixedStream.read());
        assertEquals(-1, fixedStream.read());
        assertEquals(-1, fixedStream.read());
    }

    @Test
    public void testClose() throws IOException
    {
        JaxwsBugFixInputStream fixedStream = new JaxwsBugFixInputStream(mockStream);
        fixedStream.close();
        verify(mockStream).close();
    }
}
