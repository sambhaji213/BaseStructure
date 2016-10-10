package com.basestructure.util;

import android.content.Context;

import org.json.JSONObject;

public interface AppNetworkDataService {
        AppNetworkDataService parseData(JSONObject responseData, Context context);
}
