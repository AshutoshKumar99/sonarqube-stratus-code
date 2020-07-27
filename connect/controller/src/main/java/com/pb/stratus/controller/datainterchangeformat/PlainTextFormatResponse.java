package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.core.common.Preconditions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class PlainTextFormatResponse extends BaseDataInterchangeFormatResponse
{
    @Override
    public void send(HttpServletResponse response, Object results) throws IOException {
        Preconditions.checkNotNull(results, "results cannot be null");
        if(!(results instanceof PlainTextConvertible))
        {
            throw new IllegalArgumentException("results should be instanceof PlainTextConvertible");
        }
        PlainTextConvertible plainTextConvertible = (PlainTextConvertible)results;
        String responseString = plainTextConvertible.getText();
        byte[] bs;
        try
        {
            bs = responseString.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x)
        {
            // UTF-8 must be supported at the minimum so we can safely
            // ignore it here
            throw new Error(x);
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(bs.length);
        OutputStream os = response.getOutputStream();
        os.write(bs);
        os.flush();
    }
}
