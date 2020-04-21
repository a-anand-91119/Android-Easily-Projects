package com.a3solutions.mecha_loca;

/**
 * Created by Anand on 13-04-2017.
 */

public class MechanicConfig {
    public static String[] id;
    public static String[] mechanicName;
    public static String[] latitude;
    public static String[] longitude;
    public static String[] distance;
    public static String[] mechId;
    public static String[] rating;
    public static String[] nrating;

    public static final String ID = "id";
    public static final String MECHANIC_NAME = "name";
    public static final String MECHID = "mech_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISTANCE = "distance";
    public static final String RATING = "rating";
    public static final String NRATING = "nrating";

    public MechanicConfig(int i) {
        id = new String[i];
        mechanicName= new String[i];
        latitude=new String[i];
        longitude=new String[i];
        distance=new String[i];
        mechId=new String[i];
        rating=new String[i];
        nrating=new String[i];
    }
}