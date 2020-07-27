package com.pb.stratus.controller.util;

import com.mapinfo.midev.service.featurecollection.v1.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

public class TypeConversionUtils
{
    private static final Logger logger = LogManager.getLogger(
            TypeConversionUtils.class);

    public static String getStringValue(String name, Map parameters,
            String defaultValue)
    {
        String[] values = (String[]) parameters.get(name);

        if (values == null)
        {
            return defaultValue;
        }
        else if (values.length == 0 || values[0] == null)
        {
            return defaultValue;
        }

        if (values.length > 1)
        {
            logger.warn("Multiple definitions of the same parameter are not "
                    + "supported. Attempting to use the first available value");
        }

        return values[0];
    }

    public static Integer getIntegerValue(String name, Map parameters,
            Integer defaultValue)
    {
        String[] values = (String[]) parameters.get(name);

        if (values == null)
        {
            return defaultValue;
        }
        else if (values.length == 0 || values[0] == null)
        {
            return defaultValue;
        }

        if (values.length > 1)
        {
            logger.warn("Multiple definitions of the same parameter are not "
                    + "supported. Using the first available value");
        }

        try
        {
            return Integer.valueOf(values[0]);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }

    public static Double getDoubleValue(String name, Map parameters,
            Double defaultValue)
    {
        String[] values = (String[]) parameters.get(name);

        if (values == null)
        {
            return defaultValue;
        }
        else if (values.length == 0 || values[0] == null)
        {
            return defaultValue;
        }

        if (values.length > 1)
        {
            logger.warn("Multiple definitions of the same parameter are not "
                    + "supported. Using the first available value");
        }

        try
        {
            return new Double(values[0]);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }

    public static String convertAttributeValueToString(AttributeValue attValue) {
        //AttributeValue attribValue;
        if (attValue instanceof IntValue) {
            IntValue attribIntValue = (IntValue) attValue;
            return attribIntValue.getValue().toString();

        } else if (attValue instanceof DoubleValue) {
            DoubleValue attribDoubleValue = (DoubleValue) attValue;
            return  attribDoubleValue.getValue().toString();

        }else if (attValue instanceof ShortValue) {
            ShortValue attribShortValue = (ShortValue) attValue;
            return  attribShortValue.getValue().toString();

        }else if (attValue instanceof LongValue) {
            LongValue attribLongValue = (LongValue) attValue;
            return  attribLongValue.getValue().toString();

        }else if (attValue instanceof DecimalValue) {
            DecimalValue attribDecimalValue = (DecimalValue) attValue;
            return  attribDecimalValue.getValue().toString();

        }else if (attValue instanceof FloatValue) {
            FloatValue attribFloatValue = (FloatValue) attValue;
            return  attribFloatValue.getValue().toString();

        }else if (attValue instanceof ByteValue) {
            ByteValue attribByteValue = (ByteValue) attValue;
            return  attribByteValue.getValue().toString();
        }
        else {
            throw new RuntimeException(
                    "Attribute value not of Numeric Type ");
        }

    }
}
