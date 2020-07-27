package com.pb.gazetteer.webservice;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is only used to work around a bug in the current implementation
 * of JAX-WS (http://java.net/jira/browse/JAX_WS-965).  This should be removed
 * as soon as a release with the fix is available.
 */
public class JaxwsBugFixInputStream extends InputStream
{
    private final InputStream inputStream;
    private boolean reachedEndOfInput = false;

    public JaxwsBugFixInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException
    {
        //avoid calling read on the underlying JAX-WS implementation as it
        //throws an exception after the first return of a -1.
        if (reachedEndOfInput) {
            return -1;
        }

        int result = inputStream.read();
        if (result == -1) {
            reachedEndOfInput = true;
        }
        return result;
    }

    @Override
    public void close() throws IOException
    {
        inputStream.close();
    }
}
