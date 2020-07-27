package com.pb.stratus.controller.csv;

/**
 * Interface for Csv conversion. Since CSV serialization can be highly
 * specific it cannot be generalized like json. So to keep the design simple
 * the class that needs to to be serialized to csv should return its specific
 * converter.
 */
public interface CsvConverter
{
    /**
     * Generic method which will return the CSV string from the given object.
     * @param separator char CSV field separator
     * @param lineBreak String the line break after each row of the CSV.
     * @return
     */
    public String getCsv(char separator, String lineBreak);
}
