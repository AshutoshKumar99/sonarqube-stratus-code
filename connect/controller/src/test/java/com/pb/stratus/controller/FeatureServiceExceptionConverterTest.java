package com.pb.stratus.controller;

import com.pb.stratus.controller.info.QuerySyntaxException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FeatureServiceExceptionConverterTest
{
    
    private FeatureServiceExceptionConverter converter;

    @Before
    public void setUp()
    {
        converter = new FeatureServiceExceptionConverter();
    }
    
    @Test
    public void shouldThrowRemoteAccessExceptionOnUnmatchedMessage()
    {
        try
        {
            throw converter.convert(new Exception(
                    "This message hopefully doesn't match any patterns"));
        }
        catch (RemoteAccessException rx)
        {
            // expected
        }
    }
    
    @Test
    public void shouldThrowUnknownTableExceptionOnUnknownTableMessage()
    {
        try
        {
            throw converter.convert(new Exception(
                    "Resource was not found - 'someTable'"));
        }
        catch (UnknownTableException utx)
        {
            // expected
            assertEquals("someTable", utx.getTableName());
        }
    }

    @Test
    public void shouldThrowUnknownPropertyExceptionOnColumnNotFoundMessage()
    {
        try
        {
            throw converter.convert(new Exception(
                    "Column 'someColumn' was not found in the table"));
        }
        catch (UnknownPropertyException upx)
        {
            // expected
            assertEquals("someColumn", upx.getPropertyName());
        }
    }

    @Test
    public void shouldThrowUnsupportedGeometryExceptionOnUnsupportedOperation()
    {
        try
        {
            throw converter.convert(new Exception(
                    "java.lang.UnsupportedOperationException"));
        }
        catch (UnsupportedGeometryException ugx)
        {
            // expected
        }
    }

    @Test
    public void shouldThrowUnresolvedIdentiferExceptionOnUnresolvedIdentifierMessage()
    {
        try
        {
            throw converter.convert(new Exception("LanguageEx_" 
                    + "IdentifierResolveFailure: Unable to resolve " 
                    + "expression. someIdentifier"));
        }
        catch (IdentifierResolveFailureException irfx)
        {
            // expected
            assertEquals("someIdentifier", irfx.getIdentifierName());
        }
    }

    @Test
    public void shouldThrowQuerySyntaxExceptionOnIllegalQuerySyntaxMessage()
    {
        try
        {
            throw converter.convert(new Exception(
                    "Unable to parse SQL string"));
        }
        catch (QuerySyntaxException qsx)
        {
            // expected
        }
    }

}
