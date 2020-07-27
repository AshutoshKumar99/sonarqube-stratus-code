package com.pb.stratus.controller;

/**
 * Indicates that an unknown table was requested from the FeatureService
 * 
 * @author Volker Leidl
 */
public class UnknownTableException extends RuntimeException
{
	
	private static final long serialVersionUID = -5473729916462409453L;
	
	private String tableName;
	
	public UnknownTableException(String tableName)
	{
		this.tableName = tableName;
	}
	
	public String getTableName()
	{
		return tableName;
	}

}
