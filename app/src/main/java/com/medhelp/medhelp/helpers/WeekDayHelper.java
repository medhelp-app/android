package com.medhelp.medhelp.helpers;

import android.util.SparseArray;

import com.medhelp.medhelp.model.EWeekDays;

import java.util.Calendar;
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

    public static HashMap<String, Integer> mWeekDaysCalendarMap = new HashMap<String, Integer>() {
        {
            put("sunday", Calendar.SUNDAY);
            put("monday", Calendar.MONDAY);
            put("tuesday", Calendar.TUESDAY);
            put("wednesday", Calendar.WEDNESDAY);
            put("thursday", Calendar.THURSDAY);
            put("friday", Calendar.FRIDAY);
            put("saturday", Calendar.SATURDAY);
        }
    };

    public static SparseArray<String> mWeekCalendarDaysMap = new SparseArray<String>() {
        {
            put(Calendar.SUNDAY, "sunday");
            put(Calendar.MONDAY, "monday");
            put(Calendar.TUESDAY, "tuesday");
            put(Calendar.WEDNESDAY, "wednesday");
            put(Calendar.THURSDAY, "thursday");
            put(Calendar.FRIDAY, "friday");
            put(Calendar.SATURDAY, "saturday");
        }
    };

}
