package com.pb.stratus.controller.infrastructure;

import com.pb.stratus.core.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * A class that executes a number of tasks as a single logical unit. 
 * A CombiningExecutor is done, once all the tasks that have been added to it 
 * are finished. A CombiningExecutor operates on a given ExecutorService 
 * instance. A single ExecutorService can be safely shared between multiple 
 * CombiningExecutors.
 * 
 * @param <T> the type of results this executor service deals with
 */
public class CombiningExecutor<T>
{
    
    private ExecutorService executorService;
    
    private List<Callable<T>> tasks = new LinkedList<Callable<T>>();
    

    public CombiningExecutor(ExecutorService executorService)
    {
        this.executorService = executorService;
    }
    
    public void addTask(Callable<T> task)
    {
        tasks.add(task);
    }
    
    public List<T> getResults() throws InterruptedException
    {
        List<Future<T>> futures = new LinkedList<Future<T>>();
        for (Callable<T> task : tasks)
        {
            Future<T> future = executorService.submit(task);
            futures.add(future);
        }
        List<T> results = new LinkedList<T>();
        for (Future<T> future : futures)
        {
            results.add(getResult(future));
        }
        return results;
    }
    
    private T getResult(Future<T> future) throws InterruptedException
    {
        try
        {
            return future.get();
        }
        catch (ExecutionException ex)
        {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            else if (cause instanceof Error)
            {
                throw (Error) cause;
            }
            else
            {
                throw new ExecutionRuntimeException(cause);
            }
        }
    }
    
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        CombiningExecutor<?> that = getClass().cast(o);
        return ObjectUtils.equals(this.executorService, that.executorService);
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, executorService);
        return hc;
    }

}
