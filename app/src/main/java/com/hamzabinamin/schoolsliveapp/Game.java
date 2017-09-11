package com.hamzabinamin.schoolsliveapp;

/**
 * Created by Hamza on 4/29/2017.
 */

public class Game {

    private String gameID;
    private String homeSchoolName;
    private String awaySchoolName;
    private String schoolsType;
    private String field;
    private String sport;
    private String ageGroup;
    private String team;
    private String startTime;
    private String weather;
    private String temperature;
    private String status;
    private String score;
    private String lastUpdateBy;
    private String lastUpdateTime;
    private String homeSchoolImageURL;
    private String awaySchoolImageURL;

    public Game() {
        this.gameID = "";
        this.homeSchoolName = "";
        this.awaySchoolName = "";
        this.schoolsType = "";
        this.field = "";
        this.sport = "";
        this.ageGroup = "";
        this.team = "";
        this.startTime = "";
        this.weather = "";
        this.temperature = "";
        this.status = "";
        this.score = "";
        this.lastUpdateBy = "";
        this.lastUpdateTime = "";
    }

    public Game(String gameID, String homeSchoolName, String awaySchoolName, String schoolsType, String field, String sport, String ageGroup, String team, String startTime, String weather, String temperature, String status, String score, String lastUpdateBy, String lastUpdateTime, String homeSchoolImageURL, String awaySchoolImageURL) {
        this.gameID = gameID;
        this.homeSchoolName = homeSchoolName;
        this.awaySchoolName = awaySchoolName;
        this.schoolsType = schoolsType;
        this.field = field;
        this.sport = sport;
        this.ageGroup = ageGroup;
        this.team = team;
        this.startTime = startTime;
        this.weather = weather;
        this.temperature = temperature;
        this.status = status;
        this.score = score;
        this.lastUpdateBy = lastUpdateBy;
        this.lastUpdateTime = lastUpdateTime;
        this.homeSchoolImageURL = homeSchoolImageURL;
        this.awaySchoolImageURL = awaySchoolImageURL;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setStatus(String status) {
        this.startTime = status;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void setLastUpdateTime(String lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

    public String getGameID() { return this.gameID; }

    public String getHomeSchoolName() {
        return this.homeSchoolName;
    }

    public String getAwaySchoolName() {
        return this.awaySchoolName;
    }

    public String getSchoolsType() {
        return this.schoolsType;
    }

    public String getSport() {
        return this.sport;
    }

    public String getAgeGroup() {
        return this.ageGroup;
    }

    public String getTeam() {
        return this.team;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getWeather() {
        return this.weather;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public String getStatus() {
        return this.status;
    }

    public String getScore() {
        return this.score;
    }

    public String getLastUpdateBy() {
        return this.lastUpdateBy;
    }

    public String getLastUpdateTime() { return this.lastUpdateTime; }

    public String getHomeSchoolImageURL() { return this.homeSchoolImageURL; }

    public String getAwaySchoolImageURL() { return this.awaySchoolImageURL; }

}
