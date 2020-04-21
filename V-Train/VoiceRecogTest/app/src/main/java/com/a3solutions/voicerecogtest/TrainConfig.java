package com.a3solutions.voicerecogtest;

import org.json.JSONArray;

/**
 * Created by Anand on 29-03-2017.
 */

class TrainConfig {
    public static String[] trainId;
    public static String[] trainName;
    public static String[] arriveTime;
    public static String[] departTime;
    public static JSONArray[] classes;

    public static final String TAG_TRAINID = "number";
    public static final String TAG_TRAINNAME = "name";
    public static final String TAG_ARRIVETIME = "dest_arrival_time";
    public static final String TAG_DEPARTTIME = "src_departure_time";
    public static final String TAG_JSONARRAY = "train";
    public static final String TAG_CLASSES = "classes";


    public TrainConfig(int i) {
        trainId = new String[i];
        trainName = new String[i];
        arriveTime = new String[i];
        departTime = new String[i];
        classes = new JSONArray[i];
    }
}
