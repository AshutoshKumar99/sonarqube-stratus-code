package com.pb.stratus.controller.datainterchangeformat;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PlainTextConvertibleImplTest {


    @Test
    public void testEquals()
    {
        PlainTextConvertibleImpl plainTextConvertible1 =
                new PlainTextConvertibleImpl("some string");

        PlainTextConvertibleImpl plainTextConvertible2 =
                new PlainTextConvertibleImpl("some string");

        assertEquals(plainTextConvertible1, plainTextConvertible2);
    }

    @Test
    public void testHashCode()
    {
        PlainTextConvertibleImpl plainTextConvertible1 =
                new PlainTextConvertibleImpl("some string");

        PlainTextConvertibleImpl plainTextConvertible2 =
                new PlainTextConvertibleImpl("some string");

        assertEquals(plainTextConvertible1.hashCode(),
                plainTextConvertible2.hashCode());

    }
}
