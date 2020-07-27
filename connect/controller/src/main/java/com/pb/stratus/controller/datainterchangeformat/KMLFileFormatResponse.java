package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.controller.kml.KMLConverter;
import com.pb.stratus.core.common.Preconditions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class KMLFileFormatResponse extends BaseDataInterchangeFormatResponse{

    @Override
    public void send(HttpServletResponse response, Object results)
            throws IOException {
        Preconditions.checkNotNull(results, "KMLConverter cannot be null");
        if(!(results instanceof KMLConverter))
        {
            throw new IllegalArgumentException("results should be an instanceof " +
                    "KMLConverter");
        }
        KMLConverter kmlConverter = (KMLConverter)results;
        CharSequence kmlSequence = kmlConverter.getKMLString();
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment;filename=annotation.kml");
        response.setCharacterEncoding("UTF-8");
        byte [] kml = kmlSequence.toString().getBytes("UTF-8");
        response.setContentLength(kml.length);
        OutputStream os = response.getOutputStream();
        os.write(kml);
        os.flush();
        os.close();
    }
}
