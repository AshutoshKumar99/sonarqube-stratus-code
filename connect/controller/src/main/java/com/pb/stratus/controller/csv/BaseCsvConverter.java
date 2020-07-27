package com.pb.stratus.controller.csv;


import com.pb.stratus.core.common.Preconditions;

import java.util.Iterator;
import java.util.List;

/**
 * This is the base class for csv conversion. Ith has only the primary logic
 * of converting a row and and a field into csv row and field.
 * All CsvConverter should inherit this class and use super methods to
 * serialize objects into rows and fields
 */
public abstract class BaseCsvConverter implements CsvConverter
{
    // default StringBuilder to hold the CSV string
    protected StringBuilder sb = new StringBuilder();
    public static final String NULL_FIELD = "";
    public static final String DEFAULT_LINE_BREAK = "\n";

    /**
     * Serialize a row into comma(or whatever is specified as the separator)
     * fields and end the row with the given line break.
     *
     * @param row List<String> list having all the fields for a row of the csv
     * @param separator char The separator symbol appearing between two
     * fields, usually a "," or a ";"
     * @param lineBreak String the line break string appearing at the end of
     * the row.
     */
    protected void addRow(List<String> row, char separator, String lineBreak)
    {
        Preconditions.checkNotNull(row, "row list cannot be null");
        if(lineBreak == null)
        {
            lineBreak = DEFAULT_LINE_BREAK;
        }
        for(Iterator<String> it = row.iterator(); it.hasNext();)
        {
            String field = it.next();
            addField(field, separator);
            if(it.hasNext())
            {
                sb.append(separator);
            }
        }
        sb.append(lineBreak);
    }

    /**
     * Serialize the given field into Csv by using the following rules
     * 1- If the field is null replace it with blank String
     * 2-If a '"' appears then change it to """
     * 3-If the separator appears in the field the surround the field wit ""
     * @param field String field of the csv.
     * @param separator char csv field separator.
     */
    protected void addField(String field, char separator)
    {
        if (field == null)
        {
            field = NULL_FIELD;
        }
        else
        {
            field = field.replace("\"", "\"\"");
            if (field.indexOf(separator) > -1 || field.indexOf('"') > -1)
            {
                field = '"' + field + '"';
            }
        }
        sb.append(field);
    }

    /**
     * This method is used for testing.
     * @return String content of the StringBuilder
     */
    public String toString()
    {
        return sb.toString();
    }
}
