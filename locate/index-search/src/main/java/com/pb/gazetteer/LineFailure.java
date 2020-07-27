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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LineFailure
{
    @XmlElement(required=true)
    private LineFailureCode failureCode;
    @XmlElement(required=true)
    private int line;

    //empty constructor required for JAX-WS
    public LineFailure()
    {
    }

    public LineFailure(LineFailureCode failureCode, int line)
    {

        this.failureCode = failureCode;
        this.line = line;
    }

    public LineFailureCode getFailureCode()
    {
        return failureCode;
    }

    public int getLine()
    {
        return line;
    }
}
