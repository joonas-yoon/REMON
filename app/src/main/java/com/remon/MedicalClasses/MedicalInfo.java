package com.remon.MedicalClasses;

import java.io.Serializable;

public class MedicalInfo implements Serializable {

    private String medical_name;
    private String addr;
    private String tel;
    private String distance;
    private String lat;
    private String lon;
    private String classify;

    public MedicalInfo(String medical_name, String addr, String tel, String distance, String lat, String lon, String classify) {
        this.medical_name = medical_name;
        this.addr = addr;
        this.distance = distance;
        this.tel = tel;
        this.lat = lat;
        this.lon = lon;
        this.classify = classify;
    }

    public String getMedicalName() {
        return medical_name;
    }

    public String getAddress() {
        return addr;
    }

    public String getTelNum() {
        return tel;
    }

    public String getDistance() {
        return distance;
    }

    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lon;
    }

    public String getClassify() {
        return classify;
    }
}
