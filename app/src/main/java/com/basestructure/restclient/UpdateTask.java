package com.basestructure.restclient;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.basestructure.base.ApplicationClass;
import com.basestructure.data.DataContract;
import com.basestructure.provider.MethodEnum;
import com.basestructure.provider.NotificationUtil;

import java.util.concurrent.Callable;

public class UpdateTask implements Callable<Boolean> {
     
    private static final String TAG = "UpdateTask";
 
    private long mUpdateId;
    private DataContract mData;
 
    public UpdateTask( DataContract data )
    {
      	mData=data;
    	mUpdateId = DataContract.getSuperUserID();
    }
 
    /**
     * Update the table with data entered by the user.
     * Set the status to "PUT" and transacting flag to "pending".
     */
    public Boolean call()
    {
        ContentResolver cr = ApplicationClass.getAppContext().getContentResolver();
        ContentValues values = new ContentValues();
        Uri uri;
        int updateCount;
         
        uri = ContentUris.withAppendedId( mData.getPendingUri(), mUpdateId );

        updateCount = cr.update( uri, values, null, null );
   
        if ( updateCount == 0 ) {
            Log.e( TAG, "Error setting update request to PENDING status." );
            NotificationUtil.errorNotify(MethodEnum.PUT);
            return false;
        }
         
        return true;
    }
     
}