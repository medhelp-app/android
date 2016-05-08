package com.medhelp.medhelp.model;

public enum EUserType {
    Patient(0),
    Doctor(1);

    private int value;

    EUserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
