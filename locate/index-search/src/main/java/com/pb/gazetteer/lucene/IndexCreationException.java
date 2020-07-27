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

package com.pb.gazetteer.lucene;

public class IndexCreationException extends Exception
{
    public IndexCreationException()
    {
    }

    public IndexCreationException(String message)
    {
        super(message);
    }

    public IndexCreationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IndexCreationException(Throwable cause)
    {
        super(cause);
    }
}
