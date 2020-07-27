package com.pb.stratus.controller.geometry;


/**
 * Specifies the interface methods for performing SRS lookup service. 
 */
public interface SrsLookup 
{
	public boolean areSrsCodesEquivalent(String srs1, String srs2);
}
