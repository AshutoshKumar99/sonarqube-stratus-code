package com.pb.stratus.core.configuration;

import com.pb.stratus.core.exception.ConfigurationException;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder.DIR_PROPERTY_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SystemPropertyCustomerConfigDirHolderTest
{
    
    @After
    public void tearDown()
    {
        System.getProperties().remove(DIR_PROPERTY_NAME);
    }
    
    @Test
    public void shouldGetCustomerConfigDirFromSystemProperty() throws Exception
    {
        File f = File.createTempFile("test", ".txt");
        f.delete();
        File dir = f.getParentFile();
        System.setProperty(DIR_PROPERTY_NAME, dir.getCanonicalPath());
        SystemPropertyCustomerConfigDirHolder dirHolder
                = new SystemPropertyCustomerConfigDirHolder();
        assertEquals(dir.getCanonicalPath(),
             dirHolder.getCustomerConfigDir().toString());
    }
    
    @Test
    public void shouldThrowExceptionIfNoSystemProperty()
    {
        try
        {
            new SystemPropertyCustomerConfigDirHolder();
            fail("No ConfigurationException thrown");
        }
        catch (ConfigurationException cx)
        {
            // expected
        }
    }
    
    @Test
    public void shouldThrowExceptionIfDirDoesntExist()
    {
        System.setProperty(DIR_PROPERTY_NAME, "/some/non/existent/dir");
        try
        {
            new SystemPropertyCustomerConfigDirHolder();
            fail("No ConfigurationException thrown");
        }
        catch (ConfigurationException cx)
        {
            // expected
        }
    }

    @Test
    public void shouldThrowExceptionIfDirIsAFile() throws Exception
    {
        File f = File.createTempFile("test", ".txt");
        System.setProperty(DIR_PROPERTY_NAME, f.getPath());
        try
        {
            new SystemPropertyCustomerConfigDirHolder();
            fail("No ConfigurationException thrown");
        }
        catch (ConfigurationException cx)
        {
            // expected
        }
        finally
        {
            f.delete();
        }
        
    }

}
