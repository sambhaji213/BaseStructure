package com.basestructure.restclient;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

import com.basestructure.base.ApplicationClass;
import com.basestructure.data.DataContract;
import com.basestructure.provider.MethodEnum;
import com.basestructure.provider.NotificationUtil;

import java.util.concurrent.Callable;

public class DeleteTask  implements Callable<Boolean> {
     
    private static final String TAG = "DeleteTask";
 
    private long mDeleteId;
    private DataContract mData;

    public DeleteTask( DataContract data )
    {
      	mData=data;
      	mDeleteId = DataContract.getSuperUserID();
    } 
    /**
     * Set the status to "DELETE" and transacting flag to "pending".
     */
    public Boolean call()
    {
        ContentResolver cr = ApplicationClass.getAppContext().getContentResolver();
        Uri uri;
        int deleteCount;
 
        uri = ContentUris.withAppendedId(
        		mData.getPendingUri(), 
                mDeleteId );
 
        deleteCount = cr.delete( uri, null, null );
         
        if ( deleteCount == 0 ) {
            Log.e( TAG, "Error setting delete request to PENDING status." );
             
            NotificationUtil.errorNotify(MethodEnum.DELETE);
             
            return false;
        }
         
        return true;
    }
 
}