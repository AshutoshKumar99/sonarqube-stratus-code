package com.pb.stratus.util;

import java.lang.reflect.Method;

public class InvocationRetriever
{
	private Method method;
	
	private Object[] args;

	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs(Object[] args)
	{
		this.args = args;
	}
	
}
