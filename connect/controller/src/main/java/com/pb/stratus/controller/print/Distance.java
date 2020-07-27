package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

import java.text.NumberFormat;

public class Distance
{
    
    private DistanceUnit unit;
    
    private double value;
    
    public Distance(DistanceUnit unit, double value)
    {
        this.unit = unit;
        this.value = value;
    }
    
    public Distance convert(DistanceUnit targetUnit)
    {
        if (this.unit == targetUnit)
        {
            return this;
        }
        return new Distance(targetUnit, unit.convertTo(value, targetUnit));
    }
    
    public String format(NumberFormat nf)
    {
        return String.format("%s %s", nf.format(value), unit.getDisplayText());
    }
    
    public String getUnitDisplayText()
    {
        return unit.getDisplayText();
    }
    
    public DistanceUnit getUnit()
    {
        return unit;
    }
    
    public Distance multiplyBy(double factor)
    {
        double newValue = value * factor;
        return new Distance(unit, newValue);
    }
    
    public Distance getClosestRoundDistance()
    {
        double exp = Math.floor(Math.log10(value));
        double newValue = Math.round(value / Math.pow(10, exp)) 
                * Math.pow(10, exp);
        return new Distance(unit, newValue);
    }
    
    public boolean isGreaterOrEqual(Distance distance)
    {
        distance = distance.convert(unit);
        return this.value >= distance.value;
    }
    
    public double getValue()
    {
        return value;
    }
    
    public String toString()
    {
        return value + " " + unit.getDisplayText();
    }
    
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        Distance that = (Distance) o;
        if (this.unit != that.unit)
        {
            return false;
        }
        if (this.value != that.value)
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, unit);
        hc = ObjectUtils.hash(hc, value);
        return hc;
    }

}
