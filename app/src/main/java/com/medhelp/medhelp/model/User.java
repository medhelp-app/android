package com.medhelp.medhelp.model;

import java.io.Serializable;

public abstract class User implements Serializable{

    protected String _id;
    protected String name;
    protected String email;

    protected EUserType userType;

    public void User(String id, String name, String email) {
        this._id = id;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public abstract EUserType getUserType();
}
