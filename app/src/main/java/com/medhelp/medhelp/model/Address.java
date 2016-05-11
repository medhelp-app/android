package com.medhelp.medhelp.model;

import java.io.Serializable;

public class Address implements Serializable {

    private String streetName;
    private String zipCode;
    private String city;
    private String state;
    private String country;

    public Address(String streetName, String zipCode, String city, String state, String country) {
        this.streetName = streetName;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public String toString() {
        return streetName + ", " + zipCode + "\n" + city + ", " + state + ", " + country;
    }
}
