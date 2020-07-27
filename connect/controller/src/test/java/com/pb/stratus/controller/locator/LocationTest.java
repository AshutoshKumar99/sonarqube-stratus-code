package com.pb.stratus.controller.locator;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Trivial unit test class for Location object.
 * @author Colin Kirkham
 */
public class LocationTest 
{
    public LocationTest() 
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
     * Test of getId method, of class Location.
     */
    @Test
    public void getId()
    {
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        String expResult = "1";
        String result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class Location.
     */
    @Test
    public void setId()
    {
        String id = "2";
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        instance.setId(id);
        assertEquals(id, instance.getId());
    }

    /**
     * Test of getScore method, of class Location.
     */
    @Test
    public void getScore()
    {
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        float expResult = 1.0f;
        float result = instance.getScore();
        assertEquals(expResult, result, 0);
    }

    /**
     * Test of setScore method, of class Location.
     */
    @Test
    public void setScore()
    {
        float score = 0.75f;
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        instance.setScore(score);
        assertEquals(score, instance.getScore(), 0);
    }

    /**
     * Test of getName method, of class Location.
     */
    @Test
    public void getName()
    {
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        String expResult = "13A NEW STREET LONDON W1A 4WW";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Location.
     */
    @Test
    public void setName()
    {
        String name = "A TOTALLY MADE UP ADDRESS";
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        instance.setName(name);
        assertEquals(name, instance.getName());
    }

    /**
     * Test of getX method, of class Location.
     */
    @Test
    public void getX()
    {
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        double expResult = 529430.1;
        double result = instance.getX();
        assertEquals(expResult, result, 0);
    }

    /**
     * Test of setX method, of class Location.
     */
    @Test
    public void setX()
    {
        double x = 50;
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        instance.setX(x);
        assertEquals(x, instance.getX(), 0);
    }

    /**
     * Test of getY method, of class Location.
     */
    @Test
    public void getY()
    {
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        double expResult = 181507.1;
        double result = instance.getY();
        assertEquals(expResult, result, 0);
    }

    /**
     * Test of setY method, of class Location.
     */
    @Test
    public void setY()
    {
        double y = 50;
        Location instance = new Location("1", 1.0f, "13A NEW STREET LONDON W1A 4WW", 529430.1, 181507.1,"EPSG:27700");
        instance.setY(y);
        assertEquals(y, instance.getY(), 0);
    }

}