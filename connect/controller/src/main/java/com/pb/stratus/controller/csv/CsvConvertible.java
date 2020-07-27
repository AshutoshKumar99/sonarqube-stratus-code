package com.pb.stratus.controller.csv;

/**
 * Interface marking that a class supports Csv conversion. It is the
 * responsibility if such a class to return a CsvConverter which will convert
 * the object into Csv String.
 */
public interface CsvConvertible
{
    /**
     * Return the CsvConverter which will convert the object into CSV String.
     * @return  CsvConverter
     */
    public CsvConverter getCsvConverter();
}
