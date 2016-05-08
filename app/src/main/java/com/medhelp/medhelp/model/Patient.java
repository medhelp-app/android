package com.medhelp.medhelp.model;

public class Patient extends User{

    public Patient(String name, String email) {
        super(name, email);
    }

    @Override
    public EUserType getUserType() {
        return EUserType.Patient;
    }
}
