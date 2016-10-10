package com.basestructure.restclient;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.basestructure.base.ApplicationClass;
import com.basestructure.provider.RestfulProvider;

import java.util.concurrent.Callable;

public class InsertTask implements Callable<Boolean> {
     
    private static final String TAG = "InsertTask";
     
    private String mTitle;
    private String mArtist;
 
    public InsertTask(String title, String artist )
    {
        mTitle = title;
        mArtist = artist;
    }
     
    /**
     * Insert a row into the table with data entered by the user.
     * Set the status to "POST" and transacting flag to "pending".
     */
    @Override
    public Boolean call()
    {
        ContentResolver cr = ApplicationClass.getAppContext().getContentResolver();
        ContentValues values = new ContentValues();
        Uri uri;
 
        //values.put( TableConstants.COL_TITLE, mTitle );
        //values.put( TableConstants.COL_ARTIST, mArtist );
 
        uri = cr.insert( RestfulProvider.CONTENT_URI_PROFILE_PENDING, values );
        if ( uri == null ) {
            Log.e( TAG, "Error setting insert request to PENDING status." );
             
          //  NotificationUtil.errorNotify( MethodEnum.POST );
             
            return false;
        }
         
        return true;
    }
     
}