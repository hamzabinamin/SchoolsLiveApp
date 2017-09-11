package com.hamzabinamin.schoolsliveapp;

/**
 * Created by Hamza on 7/5/2017.
 */

public class User {

    private String name;
    private String phoneNumber;
    private String lastUpdateTime;
    private String totalUpdates;

    User() {
        this.name = "";
        this.phoneNumber = "";
        this.lastUpdateTime = "";
        this.totalUpdates = "";
    }

    User(String name, String phoneNumber, String lastUpdateTime, String totalUpdates) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lastUpdateTime = lastUpdateTime;
        this.totalUpdates = totalUpdates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLastUpdateTime(String lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

    public void setTotalUpdates(String totalUpdates) { this.totalUpdates = totalUpdates; }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getLastUpdateTime() { return this.lastUpdateTime; }

    public String getTotalUpdates() { return this.totalUpdates; }
}
