package com.medhelp.medhelp.model;

public class Doctor extends User{

    private String doctorType;
    private String crm;
    private String ufCrm;

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

    public String getUfCrm() {
        return ufCrm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public void setUfCrm(String ufCrm) {
        this.ufCrm = ufCrm;
    }

    public String getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }
}
