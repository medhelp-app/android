package com.medhelp.medhelp.model;

public enum EWeekDays {
    sunday("Domingo"),
    monday("Segunda"),
    tuesday("Terça"),
    wednesday("Quarta"),
    thursday("Quinta"),
    friday("Sexta"),
    saturday("Sábado");

    private String value;

    EWeekDays(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getWeekdays() {
        EWeekDays[] weekDays = values();
        String[] weekDaysNames = new String[weekDays.length];

        for (int i = 0; i < weekDays.length; i++) {
            weekDaysNames[i] = weekDays[i].getValue();
        }

        return weekDaysNames;
    }
}
