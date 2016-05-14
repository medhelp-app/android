package com.medhelp.medhelp.model;

import java.io.Serializable;

public class User implements Serializable{

    protected String _id;
    protected String name;
    protected String email;
    protected Address address;
    protected String phone;

    protected EUserType userType;

    protected String apiKey;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String _id, String name, String email, Address address, String phone) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public EUserType getUserType() {
        return userType;
    }
}
