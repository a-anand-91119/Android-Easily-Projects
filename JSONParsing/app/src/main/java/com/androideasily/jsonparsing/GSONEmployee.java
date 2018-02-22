package com.androideasily.jsonparsing;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Anand on 14-06-2017.
 */

class GSONEmployee {

    int id; //id will be included in serialization and deserialization

    @Expose()
    int dept_code;//dept_code will be included in serialization and deserialization

    @Expose(serialize = false, deserialize = false)
    String name;//will not be included in serialization or deserialization

    @Expose(serialize = false)
    String address;//included only in deserialization

    @Expose(deserialize = false)
    String dept_name;//included only in serialization

    boolean isFree;
    List<GSONProjects> project;
}
