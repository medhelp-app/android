package com.medhelp.medhelp.model;

public class Patient extends User{

    public void Patient(String id, String name, String email) {
        super.User(id, name, email);
    }

    @Override
    public EUserType getUserType() {
        return EUserType.Patient;
    }
}
