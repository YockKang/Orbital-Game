package com.main.CoreWorks.Resources;

public class Modifier {
    private final String type;
    private final float value;
    private final String strValue;

    Modifier(String name, float val, String stringVal) {
        type = name;
        value = val;
        strValue = stringVal;
    }

    public float getValue() {
        return value;
    }

    public String getStrValue() {
        return strValue;
    }

    public String getType() {
        return type;
    }
}
