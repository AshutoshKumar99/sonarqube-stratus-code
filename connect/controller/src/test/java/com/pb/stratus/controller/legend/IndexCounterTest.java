package com.pb.stratus.controller.legend;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IndexCounterTest
{
    @Test
    public void testNextIndex()
    {
        IndexCounter counter = new IndexCounter();
        assertEquals(0, counter.nextIndex());
        assertEquals(1, counter.nextIndex());
        assertEquals(2, counter.nextIndex());
        assertEquals(3, counter.nextIndex());
    }

}
