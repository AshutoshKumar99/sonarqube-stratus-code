package com.pb.stratus.controller.info;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Trivial unit test class for Info Objects object.
 * 
 * @author vilam
 */
public class InfoTest
{
    public InfoTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test to test Info Row
     */
    @Test
    public void testInfoRow()
    {
        InfoRow instance = new InfoRow("title", "key", "description", "link", "image");

        assertEquals("title", instance.getTitle());
        assertEquals("key", instance.getKey());
        assertEquals("description", instance.getDescription());
        assertEquals("link", instance.getLink());
        assertEquals("image", instance.getImage());

        instance.setTitle("title_set");
        instance.setKey("key_set");
        instance.setDescription("description_set");
        instance.setLink("link_set");
        instance.setImage("image_set");

        assertEquals("title_set", instance.getTitle());
        assertEquals("key_set", instance.getKey());
        assertEquals("description_set", instance.getDescription());
        assertEquals("link_set", instance.getLink());
        assertEquals("image_set", instance.getImage());

    }
    /**
     * Test to test Info Row
     */
    @Test
    public void testInfoTable()
    {
        InfoRow row = new InfoRow("title", "key", "description", "link", "image");
        InfoTable instance = new InfoTable("table");
        instance.getFeatures().add(row);
        
        assertEquals("table", instance.getTablename());
        assertEquals(1, instance.getFeatures().size());
        

    }
}