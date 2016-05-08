package com.medhelp.medhelp.model;

public class Doctor extends User{

    public void Doctor(String id, String name, String email) {
        super.User(id, name, email);
    }

    @Override
    public EUserType getUserType() {
        return EUserType.Doctor;
    }
}
