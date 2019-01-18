package com.zt.task.system.okhttp;

import com.zt.task.system.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseParser {
    private JSONObject src;
    private int code;

    public ResponseParser(JSONObject o) {
        LogUtils.d("response: %s", o.toString());
        src = o;
        code = o.optInt("code", -1);
    }

    public boolean isOk() {
        return getResponseCode() == 200;
    }

    public int getResponseCode() {
        return code;
    }

    public JSONObject getData() {
        return src.optJSONObject("Data");
    }

    public String getDataString() {
        return src.optString("Data");
    }

    public JSONArray getDataArray() {
        return src.optJSONArray("Data");
    }

}
