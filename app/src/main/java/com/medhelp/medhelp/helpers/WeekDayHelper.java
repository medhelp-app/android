package com.medhelp.medhelp.helpers;

import com.medhelp.medhelp.model.EWeekDays;

import java.util.HashMap;

public class WeekDayHelper {

    public static HashMap<String, EWeekDays> mWeekDaysMap = new HashMap<String, EWeekDays>() {
        {
            put("Domingo", EWeekDays.sunday);
            put("Segunda", EWeekDays.monday);
            put("Terça", EWeekDays.tuesday);
            put("Quarta", EWeekDays.wednesday);
            put("Quinta", EWeekDays.thursday);
            put("Sexta", EWeekDays.friday);
            put("Sábado", EWeekDays.saturday);
        }
    };

}
