package com.pb.stratus.controller.print;

public class MaxNumberOfTilesExceededException extends RuntimeException
{
    
    private int max;
    
    private int actual;
    
    
    public MaxNumberOfTilesExceededException(int max, int actual)
    {
        this.max = max;
        this.actual = actual;
    }

    public int getMax()
    {
        return max;
    }

    public int getActual()
    {
        return actual;
    }
    
    public String getMessage() 
    {
        return "The maximumn number of tiles that can be processed " +
        		"was exceeded";
    }
    
    

}
