package com.medhelp.medhelp.model;

public class Doctor extends User{

    public Doctor(String name, String email) {
        super(name, email);
    }

    @Override
    public EUserType getUserType() {
        return EUserType.Doctor;
    }
}
