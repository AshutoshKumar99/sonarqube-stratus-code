package com.pb.stratus.controller.annotation;

/**
 * Created by ar009sh on 18-05-2015.
 */
public class TextAnnotationStyle extends AnnotationStyle{

    private String fontFamily;

    public TextAnnotationStyle(String color, String name, String size) {
        super(color,0,size);
        this.fontFamily = name;
    }


    public String getFontFamily() {
        return fontFamily;
    }


    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

}
