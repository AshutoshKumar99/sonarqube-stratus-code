package com.pb.gazetteer;

import junit.framework.TestCase;

public class AddressTest extends TestCase
{
    private Address thisAddress = null;
    private Address thatAddress = null;

    public AddressTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        thisAddress = new Address("1", "Pitney Bowes Mapinfo, 3rd Floor, 186 City Road, London. EC1V 2NT",
                532549.0, 182704.0, "epsg:1234");
        thatAddress = new Address("1", "Pitney Bowes Mapinfo, 3rd Floor, 186 City Road, London. EC1V 2NT",
                532549.0, 182704.0, "epsg:1234");
    }

    public void testEquals()
    {
        assertEquals(thisAddress, thatAddress);
        assertEquals(thatAddress, thisAddress);
        assertFalse(thisAddress.equals(null));
        assertTrue(thisAddress.equals(thisAddress));
    }

    public void testHashCode()
    {
        assertEquals(-1004449555, thisAddress.hashCode());
        assertEquals(-1004449555, thatAddress.hashCode());
    }

    public void testCreateAddress()
   {
        Address newAddress = new Address("1", "Pitney Bowes Mapinfo, 3rd Floor, 186 City Road, London. EC1V 2NT",
                532549.0, 182704.0, "epsg:1234");
        assertEquals(thisAddress, newAddress);
   }


}
