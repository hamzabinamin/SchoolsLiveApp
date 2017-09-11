package com.hamzabinamin.schoolsliveapp;

/**
 * Created by Hamza on 4/29/2017.
 */

public class Rank {

    private int rank;
    private String name;
    private String date;
    private int totalUpdates;

    public Rank() {
        this.rank = 0;
        this.name = "";
        this.date = "";
        this.totalUpdates = -1;
    }

    public Rank(int rank, String name, String date, int totalUpdates) {
        this.rank = rank;
        this.name = name;
        this.date = date;
        this.totalUpdates = totalUpdates;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public int getTotalUpdates() {
        return this.totalUpdates;
    }

}
