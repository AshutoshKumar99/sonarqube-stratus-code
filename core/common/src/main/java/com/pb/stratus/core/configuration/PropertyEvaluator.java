package com.pb.stratus.core.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: yo003ba
 * Date: Nov 14, 2011
 * Time: 4:25:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PropertyEvaluator {
    String getType();

    String evaluate(String value);
}
