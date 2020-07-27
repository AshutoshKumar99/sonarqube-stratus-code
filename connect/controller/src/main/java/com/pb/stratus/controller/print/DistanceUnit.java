package com.pb.stratus.controller.print;

public enum DistanceUnit
{
    
    KM(1000, "km"), M(1, "m"), MI(1609.344, "mi"), FT(0.3048, "ft");
    
    private double meters;
    
    private String displayText;
    
    private DistanceUnit(double meters, String displayText)
    {
        this.meters = meters;
        this.displayText = displayText;
    }
    
    public double convertTo(double value, DistanceUnit targetUnit)
    {
        return value * meters / targetUnit.meters;
    }
    
    public String getDisplayText()
    {
        return displayText;
    }

}
