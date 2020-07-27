package com.pb.stratus.controller.kml;

public class ExportAnnotationsAsKMLResponse implements KMLConverter{

    private CharSequence kmlString;

    public ExportAnnotationsAsKMLResponse(CharSequence kmlString)
    {
        if(kmlString == null || kmlString.length() == 0)
        {
            throw new IllegalArgumentException("kml string cannot be empty");
        }
        this.kmlString = kmlString;
    }

    @Override
    public CharSequence getKMLString() {
        return kmlString;
    }
}
