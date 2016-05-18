package com.medhelp.medhelp.model;

public class Doctor extends User{

    private String crm;

    public Doctor() {
        // POJO default required constructor
    }

    public Doctor(String name, String email) {
        super(name, email);
    }

    @Override
    public EUserType getUserType() {
        return EUserType.Doctor;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }
}
