package com.basestructure.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.basestructure.R;
import com.basestructure.base.ApplicationClass;
import com.basestructure.syncadapter.WebApiConstants;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AppLoginSession {

    private static AppLoginSession AppLoginSession =null;
    private AppLoginSession(){}
    public static AppLoginSession getSpLoginSessionInstance(){
        if(AppLoginSession ==null)
            AppLoginSession = new AppLoginSession();
        return AppLoginSession;
    }

    public static final String mHelloDoxPref = "helloDoxPref";
    public static final String mTokenKey = "loginKey";
    public static final String mPasswordKey = "passwordKey";
    public static final String mSuperUserIDKey = "superUserIDKey";
    public static final String mSuperUserProfileIDKey = "superUserIDKey";
    private static String mSessionKey= "sessionKey";
    private static String mSessionUpdateTimeKey= "mSessionUpdateTimeKey";
    private final static int mSessionUpdateTime= 10*30*1000;
    private static boolean mSessionUpdateInProgress=false;

    public static void activateLoginSession()
    {
        if(1==1)
            return;

        if( !AppAndroidUtils.isNetWorkAvailable() )
            return;

        if( ! isSessionUpdateRequired() )
            return;

        HashMap<String, String> userCredentials = getUserCredentials();
        String login = userCredentials.get(mTokenKey);
        String password = userCredentials.get(mPasswordKey);

        String msg = String.format("before login called in activate session login: %s, password: " +
                "%s", login, password);
        AppAndroidUtils.showLongToastMessage(ApplicationClass.getAppContext(), msg);

        if(StringUtils.isEmpty(login) || StringUtils.isEmpty(password)){
            startLogoutSession();
        }
        else
            getSpLoginSessionInstance().new UpdateLoginSession().execute(login, password, "login");
    }

    public static void logoutActiveSession() {
        getSpLoginSessionInstance().new UpdateLoginSession().execute(null,null,"logout");
        ResetNetworkParams();
    }

    // Class with extends AsyncTask class
    private class UpdateLoginSession extends AsyncTask<String, Void, AppMessage> {
        @Override
        protected AppMessage doInBackground(String... params) {
            if(params[2].equals("login"))
            {
                return getAuthenticationResponse(params[0],params[1]);
            }
            else {
                logoutCurrentSession();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AppMessage result) {
        }
    }

    public static String logoutCurrentSession() {
        String response=null;
        try {
            URL u = new URL(AppDataUrls.getLogOutUrl());
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            switch(responseCode)
            {
                case 200:
                    response=convertStreamToString(connection.getInputStream());
                    saveSession(null);
                    break;
                default:
                    response=null;
            }
            connection.disconnect();
        }
        catch (IOException io) {
            io.printStackTrace();
        }

        return response;
    }

    static void initializeLogoutSession(){
        try {
            URL u = new URL(AppDataUrls.getLogOutUrl());
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            switch(responseCode)
            {
                case 200:
                    break;
            }
            connection.disconnect();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
    }

    static public String getAuthenticatedSession(final String login, final String pwd) {

        if( !AppAndroidUtils.isNetWorkAvailable() ) {
            return "";
        }

        /*first logout current session*/
        initializeLogoutSession();

        /* now login */
        String sessionId= "";

        String msg = String.format("getAuthenticatedSession session login: %s, password: " +
                "%s", login, pwd);
        AppGeneralUtils.printError("getAuthenticatedSession", msg);

        AtomicInteger responseCode = new AtomicInteger();
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pwd)) {
            return sessionId;
        }
        try {
            URL u = new URL(AppDataUrls.getLoginUrl());
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            String userCredit= "username="+login+"&password="+pwd;
            wr.writeBytes(userCredit);
            wr.flush();
            wr.close();

            responseCode.set(connection.getResponseCode());
            switch(responseCode.get())
            {
                case 200:
                    sessionId=getSessionIdFromConnection(connection);
                    break;
                case 401:
                    break;
                default:
                    sessionId= "";
            }
            connection.disconnect();
        }
        catch (IOException io) {
            msg= io.toString();
        }
        msg += String.format("response code %s and new session: %s",responseCode.get(), sessionId);
        AppGeneralUtils.printError("getAuthenticatedSession", msg);
        return sessionId;
    }


    static public AppMessage getAuthenticationResponse(final String login, final String pwd) {

        AppMessage spAppMessage = new AppMessage(AppMessage.MessageTypeEnum.ERROR, "Could not login, " +
                "Please try again");

        if( !AppAndroidUtils.isNetWorkAvailable(false) ) {
            spAppMessage.setMessageType(AppMessage.MessageTypeEnum.NETWORK_ERROR);
            spAppMessage.setLongMessage(ApplicationClass.getAppContext().getString(R.string.hint_networkError));
            return spAppMessage;
        }

        initializeLogoutSession();

        String message= "Incorrect Login/Password";
        String response="";

        int responseCode= HttpURLConnection.HTTP_UNAUTHORIZED;
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pwd)) {
            spAppMessage.setLongMessage(message);
            return spAppMessage;
        }
        try {
            URL u = new URL(AppDataUrls.getLoginUrl());
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            String userCredit= "username="+login+"&password="+pwd;
            wr.writeBytes(userCredit);
            wr.flush();
            wr.close();

            responseCode = connection.getResponseCode();
            switch(responseCode)
            {
                case 200:
                    parseSessionID(connection);
                    response=convertStreamToString(connection.getInputStream());
                    spAppMessage.setKey(response);
                    spAppMessage.setMessageType(AppMessage.MessageTypeEnum.SUCCESS);
                    break;
                case 401:
                    spAppMessage.setMessage(message);
                    break;
                default:
                    spAppMessage.setMessage("Unable to login, please try after sometime");
            }
            connection.disconnect();
        }
        catch (IOException io) {
            if(responseCode== HttpURLConnection.HTTP_UNAUTHORIZED)
                spAppMessage.setMessage(message);
            else
                spAppMessage.setMessage("Unable to login, please try after sometime");
        }
        return spAppMessage;
    }

    private static void parseSessionID(HttpURLConnection connection) {
        saveSession(getSessionIdFromConnection(connection));
    }

    private static String getSessionIdFromConnection(HttpURLConnection connection) {
        String sessionId="";

        try {
            String headerInfo=connection.getHeaderField("Set-Cookie");
            if (headerInfo.contains("JSESSIONID")) {
                int index = headerInfo.indexOf("JSESSIONID=");

                int endIndex = headerInfo.indexOf(";", index);

                sessionId = headerInfo.substring(
                        index + "JSESSIONID=".length(), endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void startLogoutSession() {

        HashMap<String, String> userCredentials = getUserCredentials();
        String login = userCredentials.get(mTokenKey);
        String password = userCredentials.get(mPasswordKey);
    }


    public static void  saveSession(String sessionID){
        if(StringUtils.isNotEmpty(sessionID)) {
            long currentTime = System.currentTimeMillis();

            SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = hxPrefs.edit();
            editor.putString(mSessionKey, sessionID);
            editor.putLong(mSessionUpdateTimeKey, currentTime);

            editor.commit();
        }
    }

    public static boolean isSessionUpdateRequired(){
        long currentTime= System.currentTimeMillis();

        SharedPreferences hxPrefs =  ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref, Context.MODE_PRIVATE);
        long lastUpdateTime = hxPrefs.getLong(mSessionUpdateTimeKey, currentTime);
        return (lastUpdateTime + mSessionUpdateTime) <= currentTime;
    }

    public static String getSessionID(){
        SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref, Context.MODE_PRIVATE);
        long currentTime= System.currentTimeMillis();
        long lastUpdateTime = hxPrefs.getLong(mSessionUpdateTimeKey, currentTime);
        if(!mSessionUpdateInProgress && ((lastUpdateTime + mSessionUpdateTime) <= currentTime)){
            mSessionUpdateInProgress=true;
            String sessionId = getAuthenticatedSession(hxPrefs.getString(mTokenKey, ""),
                    hxPrefs.getString(mPasswordKey, ""));
            saveSession(sessionId);
            mSessionUpdateInProgress=false;
            return sessionId;
        }
        return hxPrefs.getString(mSessionKey,"");
    }

    public static void saveUserCredentials(String login, String password, String userId, String profileId){

        SharedPreferences hxPrefs =  ApplicationClass.getAppContext().getSharedPreferences
                (mHelloDoxPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hxPrefs.edit();
        editor.putString(mTokenKey, login);
        editor.putString(mPasswordKey, password);
        editor.putString(mSuperUserIDKey, userId);
        editor.putString(mSuperUserProfileIDKey, profileId);
        editor.commit();
    }

    public static String getSuperUserId(){
        SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref, Context.MODE_PRIVATE);
        return hxPrefs.getString(mSuperUserIDKey, "");
    }

    public static String getSuperUserProfileId(){
        SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref, Context.MODE_PRIVATE);
        return hxPrefs.getString(mSuperUserProfileIDKey, "");
    }

    public static HashMap<String, String > getUserCredentials(){
        HashMap<String, String> userCredentials = new HashMap<>();

        SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences
                (mHelloDoxPref, Context.MODE_PRIVATE);

        userCredentials.put(mTokenKey, hxPrefs.getString(mTokenKey, ""));
        userCredentials.put(mPasswordKey, hxPrefs.getString(mPasswordKey, ""));
        userCredentials.put(mSuperUserIDKey, hxPrefs.getString(mSuperUserIDKey, ""));
        userCredentials.put(mSuperUserProfileIDKey, hxPrefs.getString(mSuperUserProfileIDKey, ""));
        return userCredentials;
    }

    private static void ResetNetworkParams(){
        SharedPreferences hxPrefs = ApplicationClass.getAppContext().getSharedPreferences(mHelloDoxPref,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hxPrefs.edit();
        editor.clear();
        editor.commit();
    }

    public final static void addSessionCookie(Map<String, String> headers)
    {
        String sessionId = getSessionID();
        if (sessionId.length() > 0)
        {
            StringBuilder builder = new StringBuilder();
            builder.append("JSESSIONID");
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey("Cookie")) {
                builder.append("; ");
                builder.append(headers.get("Cookie"));
            }
            headers.put("Cookie", builder.toString());

        }
        headers.put(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
        headers.put(WebApiConstants.HEADER_ACCEPT_TOKEN_PARM, WebApiConstants.HEADER_TOKEN_PREFIX);
    }


    public static boolean isDataUpdateRequired(int lastUpdateTime){
        long currentTime= System.currentTimeMillis();
        return (lastUpdateTime + mSessionUpdateTime) <= currentTime;
    }
}
