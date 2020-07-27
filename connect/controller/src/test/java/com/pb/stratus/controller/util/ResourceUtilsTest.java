package com.pb.stratus.controller.util;


import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourceUtilsTest
{

    @Before
    public void setUp() throws Exception
    {}

    @After
    public void tearDown() throws Exception
    {}

    @Test
    public void testIsValidPositive()
    {
        String fileName = "defaultmap.xml";
        boolean isValid = ResourceUtils.isValidFilename(fileName);
        Assert.assertEquals(true, isValid);
    }
    
    @Test
    public void testIsValidFileNameNull()
    {
        String fileName = null;
        boolean isValid = ResourceUtils.isValidFilename(fileName);
        Assert.assertEquals(false, isValid);
    }

    @Test
    public void testIsValidSpecialChars()
    {
         boolean isValid = ResourceUtils.isValidFilename("test\\test"); 
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test:test") : isValid; 
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test*test") : isValid;
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test?test") : isValid; 
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test>test") : isValid;
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test<test") : isValid; 
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test\"test") : isValid;
         isValid = (!isValid) ? ResourceUtils.isValidFilename("test|test") : isValid;
         Assert.assertEquals(false, isValid);
    }
}
