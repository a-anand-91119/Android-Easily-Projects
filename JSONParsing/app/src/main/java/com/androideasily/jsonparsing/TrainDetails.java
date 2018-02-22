package com.androideasily.jsonparsing;


import java.util.List;
import java.util.Map;

/**
 * Created by Anand on 13-06-2017.
 */

public class TrainDetails {

    private int no, number;
    private String name, dept_time, arriv_time, travel_time;
    private Map<String, String> fromStation, toStation;
    private List<ClassDetails> classDetails;
    private List<DayDetails> dayDetails;

    void setDayDetails(List<DayDetails> dayDetails) {
        this.dayDetails = dayDetails;
    }

    void setClassDetails(List<ClassDetails> classDetails) {
        this.classDetails = classDetails;
    }

    public void setNo(int no) {
        this.no = no;
    }

    void setNumber(int number) {
        this.number = number;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDept_time(String dept_time) {
        this.dept_time = dept_time;
    }

    void setArriv_time(String arriv_time) {
        this.arriv_time = arriv_time;
    }

    void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    void setFromStation(Map<String, String> fromStation) {
        this.fromStation = fromStation;
    }

    void setToStation(Map<String, String> toStation) {
        this.toStation = toStation;
    }

}
