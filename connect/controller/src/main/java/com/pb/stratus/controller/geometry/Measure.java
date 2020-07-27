package com.pb.stratus.controller.geometry;


import com.pb.stratus.core.util.ObjectUtils;

public class Measure {
    private String measureType;
    private double measureValue;
    private String measureUnit;

    public Measure(String measureType, double measureValue, String measureUnit)
    {
        this.measureType = measureType;
        this.measureValue = measureValue;
        this.measureUnit = measureUnit;
    }

    public String getMeasureType() {
        return measureType;
    }

    public double getMeasureValue() {
        return measureValue;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public boolean equals(Object obj)
    {
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof Measure))
        {
            return false;
        }
        if(obj == this)
        {
            return true;
        }
        Measure that = (Measure)obj;
        return this.measureType.equals(that.measureType) && this.measureUnit.equals(
                that.measureUnit) && this.measureValue == that.measureValue;
    }

    public int hashCode()
    {
        int hash = ObjectUtils.hash(ObjectUtils.SEED, measureType);
        hash = ObjectUtils.hash(hash, measureValue);
        hash = ObjectUtils.hash(hash, measureUnit);
        return hash;
    }
}
