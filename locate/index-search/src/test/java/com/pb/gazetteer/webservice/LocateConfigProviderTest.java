package com.pb.gazetteer.webservice;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.pb.gazetteer.ConfigurationException;
import com.pb.gazetteer.ConfigurationReader;
import com.pb.gazetteer.IndexSearchFactory;
import com.pb.gazetteer.LocateConfig;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest(LocateConfigProvider.class)
public class LocateConfigProviderTest {

    @Test
    public void testGetConfig_valid() throws Exception {
        ConfigurationReader reader = mock(ConfigurationReader.class);
        whenNew(ConfigurationReader.class).withAnyArguments().thenReturn(reader);
        LocateConfig expectedConfig = new LocateConfig();
        when(reader.getLocateConfig()).thenReturn(expectedConfig);

        HashMap<String, IndexSearchFactory> testFactoryMap = new HashMap<String, IndexSearchFactory>();
        File tempFile = File.createTempFile("LocateConfigProviderTest", "testGetConfig");
        String testConfigDir = tempFile.getParent();
        tempFile.delete();
        LocateConfigProvider lcp = new LocateConfigProvider(testConfigDir, testFactoryMap);
        String testTenant = "testTenant";
        LocateConfig result = lcp.getConfig(testTenant);
        assertSame(expectedConfig, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfig_emptyName() throws IOException
    {
        HashMap<String, IndexSearchFactory> testFactoryMap = new HashMap<String, IndexSearchFactory>();
        File tempFile = File.createTempFile("LocateConfigProviderTest", "testGetConfig");
        String testConfigDir = tempFile.getParent();
        tempFile.delete();
        LocateConfigProvider lcp = new LocateConfigProvider(testConfigDir, testFactoryMap);
        LocateConfig result = lcp.getConfig("");
    }

    @Test(expected = ConfigurationException.class)
    public void testGetConfig_invalidName() throws IOException
    {
        HashMap<String, IndexSearchFactory> testFactoryMap = new HashMap<String, IndexSearchFactory>();
        File tempFile = File.createTempFile("LocateConfigProviderTest", "testGetConfig");
        String testConfigDir = tempFile.getParent();
        tempFile.delete();
        LocateConfigProvider lcp = new LocateConfigProvider(testConfigDir, testFactoryMap);
        LocateConfig result = lcp.getConfig("someWrongTenant");
    }
}
