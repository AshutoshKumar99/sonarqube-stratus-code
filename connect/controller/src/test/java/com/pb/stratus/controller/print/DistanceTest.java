package com.pb.stratus.controller.print;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DistanceTest
{
    
    @Test
    public void shouldGetNextRoundDistance()
    {
        assertEquals(new Distance(DistanceUnit.M, 300), 
                new Distance(DistanceUnit.M, 250).getClosestRoundDistance());
        assertEquals(new Distance(DistanceUnit.M, 2000), 
                new Distance(DistanceUnit.M, 2001).getClosestRoundDistance());
    }

}
