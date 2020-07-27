package com.pb.stratus.controller;

import com.pb.stratus.controller.info.QuerySyntaxException;
import com.pb.stratus.core.util.ObjectUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An exception converter that can be used in conjunction with the 
 * MI Dev Feature Service.
 */
public class FeatureServiceExceptionConverter implements ExceptionConverter
{
    
    private static Pattern INVALID_SRS_PATTERN;
    
    private static Pattern RESOURCE_NOT_FOUND_PATTERN;
    
    private static Pattern COLUMN_NOT_FOUND_PATTERN;
    
    private static Pattern UNSUPPORTED_GEOMETRY_PATTERN;
    
    private static Pattern UNRESOLVED_IDENTIFIER_PATTERN;
    
    private static Pattern ILLEGAL_QUERY_SYNTAX_PATTERN;
    
    static
    {
        INVALID_SRS_PATTERN = Pattern.compile(".*Invalid srsName found.*", 
                Pattern.DOTALL);
        RESOURCE_NOT_FOUND_PATTERN = Pattern.compile(
                ".*Resource was not found - '(.*?)'.*", 
                Pattern.DOTALL); 
        COLUMN_NOT_FOUND_PATTERN = Pattern.compile(
                ".*Column '(.*?)' was not found in the table.*", 
                Pattern.DOTALL); 
        UNSUPPORTED_GEOMETRY_PATTERN = Pattern.compile(
                "java.lang.UnsupportedOperationException", 
                Pattern.DOTALL); 
        UNRESOLVED_IDENTIFIER_PATTERN = Pattern.compile(
                ".*LanguageEx_IdentifierResolveFailure: Unable to resolve " 
                        + "expression. (.*?)", 
                Pattern.DOTALL); 
        ILLEGAL_QUERY_SYNTAX_PATTERN = Pattern.compile(
                ".*Unable to parse SQL string.*", Pattern.DOTALL);
        
    }

    public RuntimeException convert(Exception x)
    {
        String s = x.getMessage();
        Matcher m = INVALID_SRS_PATTERN.matcher(s);
        if (m.matches())
        {
            return new UnknownSrsException();
        }
        m = RESOURCE_NOT_FOUND_PATTERN.matcher(s);
        if (m.matches())
        {
            String tableName = null;
            if (m.groupCount() == 1)
            {
                tableName = m.group(1);
            }
            return new UnknownTableException(tableName);
        }
        m = COLUMN_NOT_FOUND_PATTERN.matcher(s);
        if (m.matches())
        {
            String attrName = null; 
            if (m.groupCount() == 1)
            {   
                attrName = m.group(1);
            }
            return new UnknownPropertyException(attrName);
        }
        m = UNSUPPORTED_GEOMETRY_PATTERN.matcher(s);
        if (m.matches())
        {
            return new UnsupportedGeometryException();
        }
        m = UNRESOLVED_IDENTIFIER_PATTERN.matcher(s);
        if (m.matches())
        {
            String identifierName = null;
            if (m.groupCount() == 1)
            {
                identifierName = m.group(1);
            }
            return new IdentifierResolveFailureException(identifierName);
        }
        m = ILLEGAL_QUERY_SYNTAX_PATTERN.matcher(s);
        if (m.matches())
        {
            return new QuerySyntaxException(x);
        }
        else
        {
            return new RemoteAccessException(x);
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        return obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.SEED * getClass().hashCode();
    }

}
