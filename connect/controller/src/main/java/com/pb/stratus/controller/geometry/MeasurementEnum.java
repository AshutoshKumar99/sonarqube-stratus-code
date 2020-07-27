package com.pb.stratus.controller.geometry;


public enum MeasurementEnum {
    AREA("area"), LENGTH("length"), PERIMETER("perimeter");

    private String measurementType;

    MeasurementEnum(String measurementType)
    {
        this.measurementType = measurementType;
    }

    public String value() {
        return this.measurementType;
    }

    public static MeasurementEnum getMeasurement(String measurementType)
    {
        for(MeasurementEnum measurementEnum : MeasurementEnum.values())
        {
            if(measurementEnum.measurementType.equals(measurementType))
            {
                return measurementEnum;
            }
        }
        throw new IllegalArgumentException("Measurement type "+  measurementType +
                "not supported");
    }
}
