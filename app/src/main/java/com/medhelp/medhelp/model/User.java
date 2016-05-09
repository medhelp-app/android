package com.medhelp.medhelp.model;

import java.io.Serializable;

public class User implements Serializable{

    protected String _id;
    protected String name;
    protected String email;
    protected String location;
    protected String phone;

    protected EUserType userType;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EUserType getUserType() {
        return userType;
    }
}
