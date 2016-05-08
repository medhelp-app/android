package com.medhelp.medhelp.model;

import java.io.Serializable;

public class User implements Serializable{

    private String _id;
    private String name;
    private String email;

    public void User(String id, String name, String email, String password) {
        this._id = id;
        this.name = name;
        this.email = email;
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
}
