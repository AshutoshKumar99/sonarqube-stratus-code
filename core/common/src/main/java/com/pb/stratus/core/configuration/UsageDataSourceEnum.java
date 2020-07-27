package com.pb.stratus.core.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/24/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public enum UsageDataSourceEnum {
    CSV("csv"),
    MYSQL("mysql"),
    NONE("none");
    private String value;

    UsageDataSourceEnum(String value){
        this.value = value;
    }

    private String getValue(){
        return value;
    }

    public static UsageDataSourceEnum getDataSource(String value){
        for(UsageDataSourceEnum source: UsageDataSourceEnum.values()){
            if(source.value.equalsIgnoreCase(value)){
                return source;
            }
        }
        return NONE;
    }

}
