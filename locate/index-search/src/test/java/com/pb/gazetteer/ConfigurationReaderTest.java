package com.pb.gazetteer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pb.gazetteer.lucene.LuceneIndexSearchFactory;

public class ConfigurationReaderTest
{
    private LocateConfig locateConfig;
    private String configFilePath = "src/test/resources/com/pb/gazetteer/test-config.xml";
    @Before
    public void setUp() throws Exception
    {
        ConfigurationReader reader = new ConfigurationReader(new File(configFilePath),
            Collections.<String, IndexSearchFactory>singletonMap(LuceneIndexSearchFactory.class.getName(),
                    new LuceneIndexSearchFactory(1,1)));
        locateConfig = reader.getLocateConfig();
    }

    @Test
    public void testGetLocateConfig() throws Exception
    {
        assertEquals("CamdenNLPG", locateConfig.getDefaultInstance().getName());

        List<LocateInstance> list = locateConfig.getLocateInstances();
        LocateInstance LuceneIns = (LocateInstance) list.get(0);
        assertEquals("CamdenNLPG", LuceneIns.getName());
    }
}
