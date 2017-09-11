package com.hamzabinamin.schoolsliveapp;

/**
 * Created by Hamza on 8/20/2017.
 */

public class Chat {

    private String phoneNumber;
    private String name;
    private String message;
    private String time;

    public Chat() {
        this.phoneNumber = "";
        this.name = "";
        this.message = "";
        this.time = "";
    }

    public Chat(String phoneNumber, String name, String message, String time) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTime() {
        return this.time;
    }
}
