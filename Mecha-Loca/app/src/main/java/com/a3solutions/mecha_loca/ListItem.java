package com.a3solutions.mecha_loca;

/**
 * Created by Anand on 13-04-2017.
 */

public class ListItem {
    private String id;
    private String mechanicName;
    private String latitude;
    private String longitude;
    private String distance;
    private String mechId;
    private String userId;
    private String userLatitude;
    private String userLongitude;
    private String rating;
    private String nrating;

    public String getNrating() {
        return nrating;
    }

    public void setNrating(String nrating) {
        this.nrating = nrating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMechId() {
        return mechId;
    }

    public void setMechId(String mechId) {
        this.mechId = mechId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}
