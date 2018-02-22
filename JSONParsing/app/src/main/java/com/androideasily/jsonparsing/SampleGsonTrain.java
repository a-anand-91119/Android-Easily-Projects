package com.androideasily.jsonparsing;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Anand on 14-06-2017.
 */

public class SampleGsonTrain {

    int no;
    String name;
    int number;
    @SerializedName("src_departure_time")
    String departureTime;
    @SerializedName("dest_arrival_time")
    String arrivalTime;
    @SerializedName("travel_time")
    String travelTime;
    SampleGsonFromTo from;
    SampleGsonFromTo to;
    List<SampleGsonClasses> classes;
    List<SampleGsonDays> days;
}
