package com.medhelp.medhelp.model;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Appointment {

    public String _id;
    public String date;
    public User user;
    public Availability availabilityId;

    public Appointment() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("T");

        String[] day = dates[0].split("-");
        String[] hour = dates[1].split(":");
        calendar.set(Integer.parseInt(day[0]), Integer.parseInt(day[1]) - 1, Integer.parseInt(day[2]));

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));
        return df.format(calendar.getTime());
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Availability getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Availability availabilityId) {
        this.availabilityId = availabilityId;
    }
}
