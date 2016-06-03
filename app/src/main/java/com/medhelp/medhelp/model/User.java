package com.medhelp.medhelp.model;

import java.io.Serializable;

public class User implements Serializable{

    protected String _id;
    protected String name;
    protected String email;
    protected String phone;

    protected String addressStreet;
    protected String addressNumber;
    protected String zipCode;
    protected String city;
    protected String state;
    protected String country;

    protected String profileImage;

    protected EUserType userType;


    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String _id, String name, String email, String phone) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddressStreet() {
        if (addressStreet == null) {
            return "";
        }
        return addressStreet;
    }

    public String getAddressNumber() {
        if (addressNumber == null) {
            return "";
        }
        return addressNumber;
    }

    public String getZipCode() {
        if (zipCode == null) {
            return "";
        }
        return zipCode;
    }

    public String getCity() {
        if (city == null) {
            return "";
        }
        return city;
    }

    public String getState() {
        if (state == null) {
            return "";
        }
        return state;
    }

    public String getCountry() {
        if (country == null) {
            return "";
        }
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EUserType getUserType() {
        return userType;
    }

    public void insertAddress(String streetName, String streetNumber, String zipCode, String city,
                              String state, String country) {
        this.addressStreet = streetName;
        this.addressNumber = streetNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullAddress() {
        return getAddressStreet() + " " + getAddressNumber() + " " + getZipCode() + "\n" + getCity()
                + " " + getState() + " " + getCountry();
    }
}
