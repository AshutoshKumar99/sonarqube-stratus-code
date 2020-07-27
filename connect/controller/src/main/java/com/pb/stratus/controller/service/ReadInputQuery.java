package com.pb.stratus.controller.service;

import com.pb.stratus.controller.queryutils.QueryMetadata;

/**
 * Provides methods for creating input query .
 */
public interface ReadInputQuery {

	/*
	 * This class returns Query data from a XML
	 */
	public QueryMetadata createQueryMetadata(SearchByQueryParams params);
	
	
}
