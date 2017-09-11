package com.hamzabinamin.schoolsliveapp;

/**
 * Created by Hamza on 6/9/2017.
 */

public class School {

    private String schoolID;
    private String schoolName;
    private String schoolType;
    private String schoolWebsite;
    private String schoolTwitter;
    private String schoolFacebook;
    private String schoolLocation;
    private String schoolImage;

    public School() {

        schoolID = "";
        schoolName = "";
        schoolType = "";
        schoolWebsite = "";
        schoolTwitter = "";
        schoolFacebook = "";
        schoolLocation = "";
        schoolImage = "";
    }

    public School(String schoolName, String schoolType, String schoolWebsite, String schoolTwitter, String schoolFacebook, String schoolLocation, String schoolImage) {

        this.schoolID = "-1";
        this.schoolName = schoolName;
        this.schoolType = schoolType;
        this.schoolWebsite = schoolWebsite;
        this.schoolTwitter = schoolTwitter;
        this.schoolFacebook = schoolFacebook;
        this.schoolLocation = schoolLocation;
        this.schoolImage = schoolImage;
    }

    public School(String schoolName, String schoolType, String schoolWebsite, String schoolTwitter, String schoolFacebook, String schoolLocation) {

        this.schoolID = "-1";
        this.schoolName = schoolName;
        this.schoolType = schoolType;
        this.schoolWebsite = schoolWebsite;
        this.schoolTwitter = schoolTwitter;
        this.schoolFacebook = schoolFacebook;
        this.schoolLocation = schoolLocation;
    }

    public void setSchoolID(String schoolID) {  this.schoolID = schoolID; }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public void setSchoolWebsite(String schoolWebsite) {
        this.schoolWebsite = schoolWebsite;
    }

    public void setSchoolTwitter(String schoolTwitter) {
        this.schoolTwitter = schoolTwitter;
    }

    public void setSchoolFacebook(String schoolFacebook) {
        this.schoolFacebook = schoolFacebook;
    }

    public void setSchoolLocation(String schoolLocation) {
        this.schoolLocation = schoolLocation;
    }

    public void setSchoolImage(String schoolImage) { this.schoolImage = schoolImage; }

    public String getSchoolID() { return this.schoolID; }

    public String getSchoolName() {
        return this.schoolName;
    }

    public String getSchoolType() {
        return this.schoolType;
    }

    public String getSchoolWebsite() {
        return this.schoolWebsite;
    }

    public String getSchoolTwitter() {
        return this.schoolTwitter;
    }

    public String getSchoolFacebook() {
        return this.schoolFacebook;
    }

    public String getSchoolLocation() {
        return this.schoolLocation;
    }

    public String getSchoolImage() { return this.schoolImage; }

/*    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            School school = (School) object;
            if (this.schoolName == school.schoolName) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.schoolName.hashCode();
        return hash;
    } */
}
