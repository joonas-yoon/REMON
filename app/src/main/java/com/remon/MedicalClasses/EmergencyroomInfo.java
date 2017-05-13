package com.remon.MedicalClasses;


import java.io.Serializable;

public class EmergencyroomInfo implements Serializable {

    private String hospital_name;
    private String addr;
    private String tel;
    private String lat;
    private String lon;
    private String accept_emergency_room;
    private String accept_operation_room;
    private String accept_patient_room;
    private double distance;

    public EmergencyroomInfo(String hospital_name, String addr, String tel, String lat, String lon, String accept_emergency_room, String accept_operation_room, String accept_patient_room) {
        this.hospital_name = hospital_name;
        this.addr = addr;
        this.tel = tel;
        this.lat = lat;
        this.lon = lon;
        this.accept_emergency_room = accept_emergency_room;
        this.accept_operation_room = accept_operation_room;
        this.accept_patient_room = accept_patient_room;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getTelNum() {
        return tel;
    }

    public String getHospitalName() {
        return hospital_name;
    }

    public String getAddress() {
        return addr;
    }

    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lon;
    }

    public String getAcceptEmergencyRoom() {
        return accept_emergency_room;
    }

    public String getAcceptOperationRoom() {
        return accept_operation_room;
    }

    public String getAcceptPatientRoom() {
        return accept_patient_room;
    }

}
