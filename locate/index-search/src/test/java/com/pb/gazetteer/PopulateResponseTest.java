/*******************************************************************************
 * Copyright (c) 2011, Pitney Bowes Software Inc.
 * All  rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 ******************************************************************************/

package com.pb.gazetteer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PopulateResponseTest
{
    @Test
    public void testGetSuccess() throws Exception
    {
        PopulateResponse pr = new PopulateResponse();
        pr.setSuccess(true);
        assertTrue(pr.getSuccess());
    }

    @Test
    public void testGetFailureCode() throws Exception
    {
        PopulateResponse pr = new PopulateResponse();
        pr.setFailureCode(FailureCode.EXCEEDED_MAX_FAILURES);
        assertEquals(FailureCode.EXCEEDED_MAX_FAILURES, pr.getFailureCode());
    }

    @Test
    public void testGetLineFailures() throws Exception
    {
        PopulateResponse pr = new PopulateResponse();
        ArrayList<LineFailure> lineFailures = new ArrayList<LineFailure>();
        pr.setLineFailures(lineFailures);
        assertSame(lineFailures, pr.getLineFailures());
    }

    @Test
    public void testGetRowAddedCnt() throws Exception
    {
        PopulateResponse pr = new PopulateResponse();
        pr.setRowAddedCnt(7);
        assertEquals(7, pr.getRowAddedCnt());
    }
}
