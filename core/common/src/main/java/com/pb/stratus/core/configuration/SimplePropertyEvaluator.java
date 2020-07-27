package com.pb.stratus.core.configuration;

public class SimplePropertyEvaluator implements PropertyEvaluator {
    public String getType() {
        return "smpl";
    }

    public String evaluate(String value) {
        return value;
    }
}
