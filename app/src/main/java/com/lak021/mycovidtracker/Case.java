package com.lak021.mycovidtracker;

import java.util.Date;
import java.util.UUID;

public class Case {
    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean mWasCloseContact;
    private String mContacts;
    private String mDuration;
    private double mLatitude;
    private double mLongitude;

    public Case() {
        this(UUID.randomUUID());
    }
    public Case(UUID id) {
        mID = id;
        mDate = new Date();
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isWasCloseContact() {
        return mWasCloseContact;
    }

    public void setWasCloseContact(boolean wasCloseContact) {
        mWasCloseContact = wasCloseContact;
    }

    public String getContacts() {
        return mContacts;
    }

    public void setContacts(String contact) {
        mContacts = contact;
    }

    public String getPhotoFilename() {
        return "IMG_" + getID().toString() + ".jpg";
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
