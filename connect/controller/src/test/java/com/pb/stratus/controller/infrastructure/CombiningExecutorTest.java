package com.pb.stratus.controller.infrastructure;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CombiningExecutorTest
{
    
    private CombiningExecutor<Object> executor;
    
    private ExecutorService mockExecutorService;

    private Future<Object> mockFuture1;

    private Future<Object> mockFuture2;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp()
    {
        mockExecutorService = mock(ExecutorService.class);
        this.executor = new CombiningExecutor<Object>(mockExecutorService);
        Callable<Object> task1 = mock(Callable.class);
        Callable<Object> task2 = mock(Callable.class);
        mockFuture1 = mock(Future.class);
        mockFuture2 = mock(Future.class);
        when(mockExecutorService.submit(task1)).thenReturn(mockFuture1);
        when(mockExecutorService.submit(task2)).thenReturn(mockFuture2);
        executor.addTask(task1);
        executor.addTask(task2);
    }
    
    @Test
    public void shouldWaitForAllTasksToFinish() throws Exception
    {
        executor.getResults();
        verify(mockFuture1).get();
        verify(mockFuture2).get();
    }
    
    @Test
    public void shouldCombineResultsInCorrectOrder() throws Exception
    {
        Object result1 = new Object();
        Object result2 = new Object();
        when(mockFuture1.get()).thenReturn(result1);
        when(mockFuture2.get()).thenReturn(result2);
        List<Object> results = executor.getResults();
        assertEquals(Arrays.asList(result1, result2), results);
    }
    
}
