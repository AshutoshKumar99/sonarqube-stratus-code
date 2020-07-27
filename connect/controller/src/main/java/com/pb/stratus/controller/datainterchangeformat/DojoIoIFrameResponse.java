package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class DojoIoIFrameResponse  extends BaseDataInterchangeFormatResponse{

    private InputStream fileInputStream;

    public DojoIoIFrameResponse()
    {
        fileInputStream = DojoIoIFrameResponse.class.
                getResourceAsStream("DojoIoIframeHtml.html");
    }

    @Override
    public void send(HttpServletResponse response, Object results)
            throws IOException {
        Preconditions.checkNotNull(results, "results cannot be null");
        if(!(results instanceof PlainTextConvertible))
        {
            throw new IllegalArgumentException(
                    "results should be instanceof PlainTextConvertible");
        }
        PlainTextConvertible plainTextConvertible = (PlainTextConvertible)results;
        String responseString = plainTextConvertible.getText();
        String htmlText = getHtmlText();
        responseString = String.format(htmlText, responseString);
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
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(bs.length);
        OutputStream os = response.getOutputStream();
        os.write(bs);
        os.flush();
    }

    private String getHtmlText() throws IOException {
        List<String> lines =
                IOUtils.readLines(this.fileInputStream, "UTF-8");
        StringBuilder sb = new StringBuilder();
        for(String line: lines)
        {
            sb.append(line);
        }
        return sb.toString();
    }
}
