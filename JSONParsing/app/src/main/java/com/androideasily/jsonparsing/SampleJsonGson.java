package com.androideasily.jsonparsing;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Anand on 14-06-2017.
 */

class SampleJsonGson {
    @SerializedName("response_code")
    int responseCode;
    int total;
    @SerializedName("train")
    List<SampleGsonTrain> trains;
}
