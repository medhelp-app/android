package com.medhelp.medhelp.model;

public enum ESeverityProblem {
    Low("Low"),
    Medium("Medium"),
    High("High");

    private String value;

    ESeverityProblem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
