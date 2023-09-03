package com.napptilus.technicalTest.domain.models.valueObjects;

import java.text.DecimalFormat;

public record Price(Double value) {

    private static final DecimalFormat formatTwoDecimals = new DecimalFormat("0.00");

    public Price {
        checkValueIsGreaterThanZero(value);
    }

    private void checkValueIsGreaterThanZero(Double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Price must be greater than 0. Received: " + value);
        }

    }

    @Override
    public String toString() {
        return formatTwoDecimals.format(value);
    }
}
