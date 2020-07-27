package com.pb.stratus.controller.action;


import com.pb.stratus.controller.InvalidGazetteerException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

public class ExportCsvDataAction extends BaseControllerAction{

    public final static String CSV_DATA = "csvData";
    public final static String FILE_NAME = "filename";
    private final static Logger logger = LogManager
            .getLogger(ExportCsvDataAction.class);

    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException,
                        InvalidGazetteerException {
        String csvData = request.getParameter(CSV_DATA);
        String filename = request.getParameter(FILE_NAME);
        if(filename == null || filename.trim().equals("")) {
            filename= "ExportData";
        }
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition","attachment;filename="+filename+".csv");
        OutputStream output = response.getOutputStream();

        try {
            IOUtils.write(csvData,output);
        } catch (SocketException e) {
            logger.error("Error occurred while writing out export data csv file  "+filename+" :" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }


}
