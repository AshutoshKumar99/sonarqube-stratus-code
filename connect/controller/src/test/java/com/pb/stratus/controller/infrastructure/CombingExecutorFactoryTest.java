package com.pb.stratus.controller.infrastructure;

import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CombingExecutorFactoryTest
{
    
    @Test
    public void shouldCreateExecutorWithExpectedExecutorService()
    {
        ExecutorService mockExecutorService = mock(ExecutorService.class);
        CombiningExecutorFactory factory = new CombiningExecutorFactory(
                mockExecutorService);
        assertEquals(new CombiningExecutor<Object>(mockExecutorService), 
                factory.createCombiningExecutor(Object.class));
    }

}
