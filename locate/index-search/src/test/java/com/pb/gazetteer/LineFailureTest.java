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

import static org.junit.Assert.assertEquals;

public class LineFailureTest
{
    @Test
    public void testGetFailureCode() throws Exception
    {
        LineFailure lf = new LineFailure(LineFailureCode.EMPTY_ADDRESS_FIELD, 0);
        assertEquals(LineFailureCode.EMPTY_ADDRESS_FIELD, lf.getFailureCode());
    }

    @Test
    public void testGetLine() throws Exception
    {
        LineFailure lf = new LineFailure(LineFailureCode.EMPTY_ADDRESS_FIELD, 5);
        assertEquals(5, lf.getLine());
    }
}
