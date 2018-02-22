package com.androideasily.jsonparsing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String
            jsonData = "{\"response_code\":200,\"total\":1,\"train\":[{\"no\":1,\"name\":\"RAPTI SAGAR EXP\",\"number\":\"12511\",\"src_departure_time\":\"06:35\",\"dest_arrival_time\":\"03:50\",\"travel_time\":\"21:15\",\"from\":{\"name\":\"GORAKHPUR JN\",\"code\":\"GKP\"},\"to\":{\"name\":\"NAGPUR\",\"code\":\"NGP\"},\"classes\":[{\"class-code\":\"FC\",\"available\":\"N\"},{\"class-code\":\"3E\",\"available\":\"N\"},{\"class-code\":\"CC\",\"available\":\"N\"},{\"class-code\":\"SL\",\"available\":\"Y\"},{\"class-code\":\"2S\",\"available\":\"N\"},{\"class-code\":\"2A\",\"available\":\"Y\"},{\"class-code\":\"3A\",\"available\":\"Y\"},{\"class-code\":\"1A\",\"available\":\"N\"}],\"days\":[{\"day-code\":\"MON\",\"runs\":\"N\"},{\"day-code\":\"TUE\",\"runs\":\"N\"},{\"day-code\":\"WED\",\"runs\":\"N\"},{\"day-code\":\"THU\",\"runs\":\"Y\"},{\"day-code\":\"FRI\",\"runs\":\"Y\"},{\"day-code\":\"SAT\",\"runs\":\"N\"},{\"day-code\":\"SUN\",\"runs\":\"Y\"}]}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Projects> projectsList = new ArrayList<>();
        projectsList.add(new Projects("Online Shopping", 10000.00d));
        projectsList.add(new Projects("Jumbled Solver", 1000.00d));
        projectsList.add(new Projects("Project Omega", 59999.00d));

        Employee employee = new Employee(100, 1000, "Anand", "12F North OG", "Research and Development", false, projectsList);
        Gson gson = new Gson();
        String jsonFormat = gson.toJson(employee);
        Log.i("JSON", jsonFormat);

        String GSONJSON = "{\"id\":100,\"dept_code\":1000,\"name\":\"Anand\",\"address\":\"12F North OG\",\"dept_name\":\"Research and Development\",\"isFree\":false}";
        String gsonWithList = "{\"address\":\"12F North OG\",\"dept_code\":1000,\"dept_name\":\"Research and Development\",\"id\":100,\"isFree\":false,\"name\":\"Anand\",\"project\":[{\"cost\":10000.0,\"name\":\"Online Shopping\"},{\"cost\":1000.0,\"name\":\"Jumbled Solver\"},{\"cost\":59999.0,\"name\":\"Project Omega\"}]}";


        GSONEmployee gsonEmployee = gson.fromJson(gsonWithList, GSONEmployee.class);

        gsonParse(jsonData);//Parsing the JSON using GSON Library
        androidParse(jsonData);//Parsing the JSON using Android Parser
    }

    private void gsonParse(String jsonData) {
        Gson gson = new Gson();
        SampleJsonGson sampleJsonGson = gson.fromJson(jsonData, SampleJsonGson.class);
        Log.i("Android Easily","GSON Parsing");
    }

    private void androidParse(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            //Fetching key-value pairs
            int response_code = jsonObject.getInt("response_code");
            int total = jsonObject.getInt("total");

            //parsing the array into JSONArray
            JSONArray trainArray = jsonObject.getJSONArray("train");
            int trainArrayLength = trainArray.length();

            List<TrainDetails> trainList = new ArrayList<>();

            for (int i = 0; i < trainArrayLength; i++) {
                //Fetching individual objects from train array
                JSONObject trainObject = trainArray.getJSONObject(i);
                TrainDetails traindetails = new TrainDetails();
                traindetails.setNo(trainObject.getInt("no"));
                traindetails.setName(trainObject.getString("name"));
                traindetails.setNumber(trainObject.getInt("number"));
                traindetails.setDept_time(trainObject.getString("src_departure_time"));
                traindetails.setArriv_time(trainObject.getString("dest_arrival_time"));
                traindetails.setTravel_time(trainObject.getString("travel_time"));

                //"from" is another JSONObject present inside the "train" JSONArray
                JSONObject fromObject = trainObject.getJSONObject("from");
                //data from the "from" JSONObject present inside the "train" JSONArray
                Map<String, String> fromStation = new HashMap<>();
                fromStation.put("name", fromObject.getString("name"));
                fromStation.put("code", fromObject.getString("code"));
                traindetails.setFromStation(fromStation);

                //"to" is also a JSONObject present inside the "train" JSONArray
                JSONObject toObject = trainObject.getJSONObject("to");
                //data from the "to" JSONObject present inside the "train" JSONArray
                Map<String, String> toStation = new HashMap<>();
                toStation.put("name", toObject.getString("name"));
                toStation.put("name", toObject.getString("name"));
                traindetails.setToStation(toStation);

                //classes is a JSONArray
                JSONArray classArray = trainObject.getJSONArray("classes");
                int classLength = classArray.length();

                //List to hold objects of ClassDetails
                List<ClassDetails> classList = new ArrayList<>();
                for (int j = 0; j < classLength; j++) {
                    //Fetching individual objects from classes array
                    JSONObject classObject = classArray.getJSONObject(j);
                    //Initializing ClassDetails object
                    ClassDetails classDetails = new ClassDetails();
                    classDetails.setClassCode(classObject.getString("class-code"));
                    classDetails.setAvailable(classObject.getString("available"));
                    classList.add(classDetails);
                }
                traindetails.setClassDetails(classList);

                //days is another JSONArray
                JSONArray daysArray = trainObject.getJSONArray("days");
                int daysLenth = daysArray.length();
                //List to hold objects of DayDetails
                List<DayDetails> dayList = new ArrayList<>();
                for (int j = 0; j < daysLenth; j++) {
                    //Fetching individual objects from days array
                    JSONObject daysObject = daysArray.getJSONObject(j);
                    //Initializing DayDetails object
                    DayDetails dayDetails = new DayDetails();
                    dayDetails.setDayCode(daysObject.getString("day-code"));
                    dayDetails.setRuns(daysObject.getString("runs"));
                    dayList.add(dayDetails);
                }
                traindetails.setDayDetails(dayList);
                trainList.add(traindetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
