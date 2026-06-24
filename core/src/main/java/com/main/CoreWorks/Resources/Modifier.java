package com.main.CoreWorks.Resources;

public class Modifier {
    private final String type;
    private float value;
    private String strValue;

    public Modifier(String name, float val, String stringVal) {
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

    public void changeValue(float change) {
        value += change;
    }

    public void setStrValue(String strVal) {
        strValue = strVal;
    }

    public String getType() {
        return type;
    }

    public String display() {
        if (strValue == null) {
            return type + ": +" + value;
        } else {
            return type + ": " + strValue;
        }
    }

    public Modifier previewChange(Modifier mod) {
        return new Modifier(type, value + mod.value, mod.strValue);
    }
}
