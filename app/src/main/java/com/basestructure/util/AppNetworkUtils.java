
package com.basestructure.util;

import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.basestructure.R;
import com.basestructure.base.ApplicationClass;

public class AppNetworkUtils {

    public static String getVolleyErrorMessage(VolleyError error){
        String errorMessage= ApplicationClass.getAppContext().getString(R.string.hint_networkError);

        if( !AppAndroidUtils.isNetWorkAvailable(true) )
            return errorMessage;
        else
            errorMessage="Session expired";

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            errorMessage="Network not available.";
        } else if (error instanceof AuthFailureError) {
            errorMessage="Session expired";
        } else if (error instanceof ServerError) {
            errorMessage="Server error, please try after sometime";
        } else if (error instanceof NetworkError) {
            errorMessage= ApplicationClass.getAppContext().getString(R.string.hint_networkError);
        } else if (error instanceof ParseError) {
            //TODO
        }
        return errorMessage;
    }

    static AsyncTask<Void, Void, Boolean> notificationSendTask;


}
