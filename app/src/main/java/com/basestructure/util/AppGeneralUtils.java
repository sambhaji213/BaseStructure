package com.basestructure.util;

import android.util.Log;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

// TODO: Auto-generated Javadoc

/**
 * The Class OEDialog.
 */
public class AppGeneralUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean preValidateOTP(String otp){
        boolean response= NumberUtils.isDigits(otp);
        return !response?response: StringUtils.length(otp)==6;
    }

    public static String parseIntFromString(String str){
        return str.replaceAll("[^0-9]", "");
    }

    public static String incodeString(String inPut){
        return StringUtils.replace(inPut, " ", "%20");
    }

    public static String formatFloat(float inPut){
        return  String.format("%.02f", inPut);
        /*DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(inPut);*/
    }

    public static String formatDouble(Double inPut){
        return  String.format("%.02f", inPut);
        /*DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(inPut);*/
    }
    public static String constructName(String title, String firstName, String midName, String lastName){

        String name = String.format("%s %s %s %s", title, firstName, midName, lastName);
        name = StringUtils.replace(name, "null", "");
        name = name.trim().replaceAll(" +", " ");
        return name;
    }

    public static void printError(String tag, String msg ){
        String method="";
        try {
            method = Thread.currentThread().getStackTrace()[3].getMethodName();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(tag, method +": "+msg);
    }
}
