package com.pb.stratus.controller.thematic;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ThematicTypeEnum {

    RANGE("Range"),
    INDIVIDUAL_VALUE("IndividualValueTheme"),
    GRID("Grid"),
    GRADUATED_SYMBOL("GraduatedSymbol"),
    DOT_DENSITY("DotDensity"),
    BAR_CHART("BarChart"),
    PIE_CHART("PieChart"),
    NONE("none");

    private String value;

    ThematicTypeEnum(String value){
        this.value = value;
    }

    private String getValue(){
        return value;
    }

    public static ThematicTypeEnum getThematicType(String value){
        for(ThematicTypeEnum type: ThematicTypeEnum.values()){
            if(type.value.equalsIgnoreCase(value)){
                return type;
            }
        }
        return NONE;
    }


}
