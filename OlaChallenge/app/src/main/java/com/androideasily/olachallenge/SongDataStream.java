package com.androideasily.olachallenge;

import org.json.JSONObject;

/**
 * Created by Anand on 16-12-2017.
 */

/*  interface to permit interfragment communication
 *  two framgments make use of this interface which is why a separate interface file and not
 *  witin a particulat fragmnet
 * */
interface SongDataStream {
    void getSongData(JSONObject data);
}
