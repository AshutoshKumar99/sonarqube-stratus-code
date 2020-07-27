package com.pb.stratus.controller.print;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.core.util.ObjectUtils;

import java.text.NumberFormat;

/**
 * Representation of scale bar information.
 */
public class ScaleBar {

    private Distance groundDistance;

    // 1 cm on the map is 1/scale cm on the ground
    private double scale;

    private NumberFormat numberFormat;

    public ScaleBar(Distance groundDistance, double scale) {
        this.groundDistance = groundDistance;
        this.scale = scale;
        numberFormat = NumberFormat.getInstance(
                LocaleResolver.getLocale());
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumFractionDigits(0);
    }

    public String getLeftLabel() {
        return new Distance(groundDistance.getUnit(), 0).format(numberFormat);
    }

    public String getMiddleLabel() {
        return groundDistance.multiplyBy(0.5).format(numberFormat);
    }

    public String getRightLabel() {
        return groundDistance.format(numberFormat);
    }

    public double getWidthOnMapInCm() {
        double widthInCm = groundDistance
                .convert(DistanceUnit.M).getValue() * 100;
        return widthInCm * scale;
    }

    protected boolean isEquivalent(ScaleBar that, double precision) {
        if (!ObjectUtils.equals(this.groundDistance, that.groundDistance)) {
            return false;
        }
        if (this.scale - that.scale > precision) {
            return false;
        }
        return true;
    }
}
