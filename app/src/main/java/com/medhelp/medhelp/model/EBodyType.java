package com.medhelp.medhelp.model;

public enum EBodyType {
    Head("head"),
    Chest("chest"),
    Abs("stomach"),
    RightArm("rightArm"),
    LeftArm("leftArm"),
    RightLeg("rightLeg"),
    LeftLeg("leftLeg");

    private String value;

    EBodyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
