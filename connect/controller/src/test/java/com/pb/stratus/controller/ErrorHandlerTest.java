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

package com.pb.stratus.controller;

import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class ErrorHandlerTest
{

    @Test
    public void testCommittedResponse() {
        ErrorHandler handler = new ErrorHandler();

        //setup the response expected state
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.isCommitted()).thenReturn(true);

        handler.convert(new RuntimeException(), mockResponse);

        verify(mockResponse).isCommitted();
        verifyNoMoreInteractions(mockResponse);
    }
}
