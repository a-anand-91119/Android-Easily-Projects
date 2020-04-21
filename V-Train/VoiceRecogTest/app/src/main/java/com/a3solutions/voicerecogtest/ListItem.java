package com.a3solutions.voicerecogtest;

import org.json.JSONArray;

/**
 * Created by Anand on 29-03-2017.
 */

class ListItem {

    private String trainId, trainName, departTime, arrivetime, dateJourney, fromStation, toStation;
    private JSONArray classes;

    public String getDateJourney() {
        return dateJourney;
    }

    public void setDateJourney(String dateJourney) {
        this.dateJourney = dateJourney;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public JSONArray getClasses() {
        return classes;
    }

    public void setClasses(JSONArray classes) {
        this.classes = classes;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public String getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }
}
