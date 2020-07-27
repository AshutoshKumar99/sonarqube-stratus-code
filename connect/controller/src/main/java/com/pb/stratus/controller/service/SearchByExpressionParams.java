package com.pb.stratus.controller.service;

public class SearchByExpressionParams extends SearchParams 
{
    
    private String expression;
    
    private String table;
    
    
    public String getTable()
    {
        return table;
    }
    
    public void setTable(String table)
    {
        this.table = table;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    
    
    

}
