package com.pb.stratus.controller.datainterchangeformat;


import com.pb.stratus.core.util.ObjectUtils;

public class PlainTextConvertibleImpl implements PlainTextConvertible{

    private String responseText;

    public PlainTextConvertibleImpl(String responseText)
    {
        this.responseText = responseText;
    }
    @Override
    public String getText() {
        return this.responseText;
    }

    public boolean equals(Object o)
    {
        if(!(o instanceof PlainTextConvertibleImpl))
        {
            return false;
        }
        if(o == this)
        {
            return true;
        }
        PlainTextConvertibleImpl that = (PlainTextConvertibleImpl)o;
        return this.responseText.equals(that.responseText);
    }

    public int hashCode()
    {
        return ObjectUtils.hash(ObjectUtils.SEED, this.responseText);
    }
}
