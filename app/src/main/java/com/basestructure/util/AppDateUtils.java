package com.basestructure.util;

import android.content.Context;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppDateUtils {

    static long ONE_MIN = 1*60*60;
    static long ONE_HRS= 1*60*60*60;

    static public long addToCurrent(int date, Integer i)
    {
        return currentDateSecs();
    }

    static public String getCurrentDate()
    {
        return dateToStringForWS(currentDateSecs());
    }
   /* static public long currentDateMillis()
    {
        Date date=new Date();
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(date);
        return stringToDateForWS(dateText);
    }
*/

    static public String currentDateInMilliSec()
    {
        return String.valueOf(System.currentTimeMillis());
    }

    static public long currentDateInMilliSecs()
    {
        return System.currentTimeMillis();
    }

    static public long currentDateSecs()
    {
        return System.currentTimeMillis()/1000;
    }

    public static long stringToDateForWS(String string_date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        long mlsec=0;
        try {
            date = formatter.parse(string_date);
            mlsec=date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mlsec;
    }

    public static String dateToStringForWS(Long dlDate) {
        Date date=new Date(dlDate);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(date);
        return dateText;
    }

    public static String dateToMilliSecond(Long dlDate) {
        Date date=new Date(dlDate);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(date);
        return dateText;
    }

    public static String getMilliSecondToDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getCurrentTime(){
        return getFormattedDate(new Date());
    }

    //format 19th Nov, 2015
    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        String format = String.format("d'%s' MMM, yyyy", getDayOfMonthSuffix(day));
        return new SimpleDateFormat(format).format(date);
    }

    static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    static public String formatTime(String timeInMilli, Context context){
        Long time = Long.valueOf(timeInMilli);
        return formatTime(time, context);
    }

    static public String formatTimeForChatHistStatus(boolean canChat, String timeInMilli, Context context){
        if(StringUtils.isEmpty(timeInMilli))
            return android.text.format.DateUtils.formatDateTime(context, currentDateInMilliSecs(),
                    android.text.format.DateUtils.FORMAT_SHOW_TIME);

        Long timeInM = Long.valueOf(timeInMilli);

        long timeDiff = currentDateInMilliSecs()- timeInM;
        if(timeDiff<3*60*1000 && canChat){
            return "online";
        }

        String displayTime="";
        boolean isToday = android.text.format.DateUtils.isToday(timeInM);
        if( isToday) {
            String today= android.text.format.DateUtils.formatDateTime(context, timeInM,
                    android.text.format.DateUtils.FORMAT_SHOW_TIME);
            displayTime = "last activity today at "+today;
        }

        String otherTime= android.text.format.DateUtils.formatDateTime(context,timeInM,
                android.text.format.DateUtils.FORMAT_ABBREV_MONTH|
                        android.text.format.DateUtils.FORMAT_SHOW_DATE|
                        android.text.format.DateUtils.FORMAT_SHOW_TIME);
        return ("last activity "+otherTime);
    }

    static public String formatTimeForChatHist(String timeInMilli, Context context){
        if(StringUtils.isEmpty(timeInMilli))
            return android.text.format.DateUtils.formatDateTime(context, currentDateInMilliSecs(),
                android.text.format.DateUtils.FORMAT_SHOW_TIME);

        Long timeInM = Long.valueOf(timeInMilli);
        boolean isToday = android.text.format.DateUtils.isToday(timeInM);
        if( isToday)
            return android.text.format.DateUtils.formatDateTime(context,timeInM,
                    android.text.format.DateUtils.FORMAT_SHOW_TIME);

        return android.text.format.DateUtils.formatDateTime(context,timeInM,
                android.text.format.DateUtils.FORMAT_ABBREV_MONTH|
                        android.text.format.DateUtils.FORMAT_SHOW_DATE|
                        android.text.format.DateUtils.FORMAT_SHOW_TIME);
    }

    static public String formatTime(long timeInMilli, Context context){
        long timeDiff = currentDateInMilliSecs() - timeInMilli;
        String formattedTime="";
        if(timeDiff<3*60*1000){
            return formattedTime;
        }
        if(timeDiff<31*60*1000){
            return String.valueOf(timeDiff/(1000*60))+ " mins";
        }
        boolean isToday = android.text.format.DateUtils.isToday(timeInMilli);
        if( isToday)
            return android.text.format.DateUtils.formatDateTime(context,timeInMilli,
                            android.text.format.DateUtils.FORMAT_SHOW_TIME);

        return android.text.format.DateUtils.formatDateTime(context,timeInMilli,
                android.text.format.DateUtils.FORMAT_ABBREV_MONTH|
                        android.text.format.DateUtils.FORMAT_SHOW_DATE|
                        android.text.format.DateUtils.FORMAT_SHOW_TIME);
    }

}
