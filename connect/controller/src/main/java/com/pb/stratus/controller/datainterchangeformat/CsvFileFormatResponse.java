package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.controller.csv.CsvConverter;
import com.pb.stratus.controller.csv.CsvConvertible;
import com.pb.stratus.controller.csv.UnknownCsvSerializableException;
import com.pb.stratus.core.common.Preconditions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is responsible for returning a CSV file as a response from the
 * given CsvConvertible object.
 */
public class CsvFileFormatResponse extends BaseDataInterchangeFormatResponse
{
    public static char SEPARATOR = ',';
    public static String LINE_BREAK = "\n";

    public void send(HttpServletResponse response, Object results)
            throws IOException
    {
        Preconditions.checkNotNull(results, "CsvConvertible cannot be null");
        if(!(results instanceof CsvConvertible))
        {
            throw new UnknownCsvSerializableException("Object must be an instanceof " +
                    "CsvConvertable.");
        }
        CsvConverter csvConverter = ((CsvConvertible)results).getCsvConverter();
        String csvSting = csvConverter.getCsv(SEPARATOR, LINE_BREAK);
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment;filename=file.csv");
        response.setCharacterEncoding("UTF-8");
        byte [] csv = csvSting.getBytes("UTF-8");
        response.setContentLength(csv.length);
        OutputStream os = response.getOutputStream();
        os.write(csv);
        os.flush();
        os.close();
    }
}
