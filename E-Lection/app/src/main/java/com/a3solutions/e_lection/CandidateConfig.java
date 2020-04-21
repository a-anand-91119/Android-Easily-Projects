package com.a3solutions.e_lection;

/**
 * Created by Anand on 25-02-2017.
 */

public class CandidateConfig {
    public static String[] id;
    public static String[] candid_id;
    public static String[] candid_name;
    public static String[] party;
    public static String[] election_id;
    public static String[] symbol;
    public static String[] election_name;


    public static final String FETCH_URL = "http://androiddevz.netai.net/e_lection/candiate_fetch.php";
    public static final String TAG_ID = "id";
    public static final String TAG_CANDID_ID = "candid_id";
    public static final String TAG_CANDID_NAME = "candid_name";
    public static final String TAG_ELECTION_ID = "election_id";
    public static final String TAG_PARTY = "party";
    public static final String TAG_SYMBOL = "symbol";
    public static final String TAG_ELECTION_NAME = "election_name";
    public static final String TAG_JSON_ARRAY = "result";

    public CandidateConfig(int i) {
        id = new String[i];
        candid_id = new String[i];
        candid_name = new String[i];
        party = new String[i];
        election_id = new String[i];
        symbol = new String[i];
        election_name = new String[i];
    }
}
