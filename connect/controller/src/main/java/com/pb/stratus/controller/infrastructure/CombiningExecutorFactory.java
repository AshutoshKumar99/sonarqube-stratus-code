package com.pb.stratus.controller.infrastructure;

import java.util.concurrent.ExecutorService;

public class CombiningExecutorFactory
{
    
    private ExecutorService executorService;

    public CombiningExecutorFactory(ExecutorService executorService)
    {
        this.executorService = executorService;
    }
    
    public <T> CombiningExecutor<T> createCombiningExecutor(Class<T> type)
    {
        return new CombiningExecutor<T>(executorService);
    }

}
