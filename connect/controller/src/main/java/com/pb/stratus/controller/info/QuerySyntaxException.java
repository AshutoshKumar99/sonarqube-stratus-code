package com.pb.stratus.controller.info;

/**
 * An exception that indicates a syntax error in the expression passed
 * to {@link FeatureServiceResolver#searchByExpression}
 * 
 * @author Volker Leidl
 */
public class QuerySyntaxException extends RuntimeException
{

	private static final long serialVersionUID = -85886317177829023L;

	public QuerySyntaxException(Throwable cause)
	{
		super(cause);
	}

}
