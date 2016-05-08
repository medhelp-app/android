package com.medhelp.medhelp.model;

import java.io.Serializable;

public class User implements Serializable{

    protected String _id;
    protected String name;
    protected String email;
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

    public EUserType getUserType() {
        return userType;
    }
}
