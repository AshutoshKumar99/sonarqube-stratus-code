package com.pb.gazetteer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PopulateResponse
{
    @XmlElement(required=true)
    private boolean success;
    @XmlElement(required=false)
    private FailureCode failureCode;
    @XmlElement(required=false)
    private List<LineFailure> lineFailures;
    @XmlElement(required=false)
    private int rowAddedCnt;

    public boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public FailureCode getFailureCode()
    {
        return failureCode;
    }

    public void setFailureCode(FailureCode failureCode)
    {
        this.failureCode = failureCode;
    }

    public List<LineFailure> getLineFailures()
    {
        return lineFailures;
    }

    public void setLineFailures(List<LineFailure> lineFailures)
    {
        this.lineFailures = lineFailures;
    }

    public int getRowAddedCnt()
    {
        return rowAddedCnt;
    }

    public void setRowAddedCnt(int rowAddedCnt)
    {
        this.rowAddedCnt = rowAddedCnt;
    }

}
