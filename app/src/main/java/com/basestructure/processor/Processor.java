package com.basestructure.processor;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.basestructure.base.ApplicationClass;
import com.basestructure.data.DataContract;
import com.basestructure.provider.Constants;
import com.basestructure.provider.MethodEnum;
import com.basestructure.provider.NotificationUtil;
import com.basestructure.provider.QueryTransactionInfo;
import com.basestructure.provider.SyncMode;

import java.util.ArrayList;

public class Processor {

    private static final String TAG = "Processor";
     
    private static final Processor instance = new Processor();
     
    private Processor() {}

    public static Processor getInstance()
    {
        return instance;
    }

    public void delete( DataContract detail )
    {
        final Context context = ApplicationClass.getAppContext();
        final ContentResolver cr = context.getContentResolver();
        int deleteCount;
 
        Uri uri = ContentUris.withAppendedId(
				detail.getCompletedUri(),
				DataContract.getUserID() );
		
        deleteCount = cr.delete(uri, null, null);
    }

    /**
     * Update the database content and set transactional
     * flags to a completed state. This method is used for
     * PUT and POST requests.
     *
     * @param detail The data information that was parsed
     * from the http response.
     */
    public void update( DataContract detail )
    {
        final Context context = ApplicationClass.getAppContext();
        final ContentResolver cr = context.getContentResolver();
      //  ContentValues values;

        Uri uri;
        uri = ContentUris.withAppendedId(
        		detail.getQueryCompletedUri(),
				DataContract.getUserID() );

/*        values = new ContentValues();
        int updateCount;

        values.put( Profile.COL_PROFILE_ID,
        		detail.getProfileId() );
        values.put( Profile.COL_PROFILE_NAME ,
        		detail.getProfileName() );
        values.put( Profile.COL_PROFILE_TITLE,
        		detail.getProfileTitle() );
        values.put( Profile.COL_PROFILE_SUMMARY,
        		detail.getProfileSummary() );
        values.put( Profile.COL_PROFILE_PERSONAL,
        		detail.getProfilePersonal() );
        values.put( Profile.COL_PROFILE_ACADMICS,
        		detail.getProfileAcadmics() );
        values.put( Profile.COL_PROFILE_PROFESSIONAL,
        		detail.getProfileProfessional() );
        values.put( Profile.COL_TRANSACTING,
        		Constants.TRANSACTION_COMPLETED );
        values.put( Profile.COL_RESULT ,
        		detail.getHttpResult() );
        values.put( Profile.COL_TRY_COUNT,
        		0 );*/
        ContentValues values=detail.getContentValues();
        int updateCount = cr.update(uri, values, null, null);

    }

    /**
     * Looping through the list of responses a separate batch operation will be
     * defined for each of the three syncModes returned from the REST API. After
     * the list is exhausted, and all the rows have been loaded into an operation,
     * the three batches will be executed.
     *
     * syncMode U = Update
     * syncMode I = Insert
     * syncMode D = Delete
     *
     * @param details Array of data information that was parsed
     * from the http response.
     * @param downloadDate Date of this most recent download
     * @param result The HttpStatus code.
     */
    public void retrieve(DataContract[] details, String downloadDate, int result )
    {
    	ContentResolver cr =   ApplicationClass.getAppContext().getContentResolver();
    	ContentValues[] insertValues;
    	SharedPreferences prefs;
    	SharedPreferences.Editor editor;
    	ArrayList< ContentProviderOperation > insertOps =
    			new ArrayList< ContentProviderOperation >();
    	ArrayList< ContentProviderOperation > updateOps =
    			new ArrayList< ContentProviderOperation >();
    	ArrayList< ContentProviderOperation > deleteOps =
    			new ArrayList< ContentProviderOperation >();

    	Uri pendingUri;

    	prefs = ApplicationClass.getAppContext().getSharedPreferences(Constants.RESTFUL_PREFS, 0 );
    	editor = prefs.edit();

    	// Put the date of this download in SharedPreferences
    	/*editor.putLong(
    			Constants.PREFS_DOWNLOAD_DATE,
    			DateUtils.stringToDateForWS( downloadDate ));
    	editor.commit();*/

    	if ( details != null ) {
    		// if success
    		insertValues = new ContentValues[details.length];
    		for ( int i = 0; i < details.length; i++ )
    		{
    			DataContract detail = details[i];
    			Uri baseUri = Uri.withAppendedPath(detail.getContentUri(),DataContract.getUserIDString());
    			Cursor cursor = cr.query(baseUri, new String[]{DataContract._ID}, null, null, null );
    			if(cursor!=null && cursor.moveToNext() )
    				detail.setSyncMode(SyncMode.U);
    			else
    				detail.setSyncMode(SyncMode.I);

    			if(cursor !=null)
    				cursor.close();

    			switch ( detail.getSyncMode()) {
    			case DataContract.U:
    				// build batch operation for updated rows
    				pendingUri = ContentUris.withAppendedId(
    						detail.getQueryCompletedUri(),
    						DataContract.getUserID() );
    				updateOps.add( detail.getUpdateOperation(pendingUri));
    				break;

    			case DataContract.I:
    				// build batch operation for inserted rows
    				insertOps.add( detail.getInsertOperation());
    				break;

    			case DataContract.D:
    				pendingUri = ContentUris.withAppendedId(
    						detail.getQueryCompletedUri(),
    						DataContract.getUserID() );
    				deleteOps.add(
    						ContentProviderOperation.newDelete(pendingUri)
    						.withYieldAllowed(true)
    						.build());

    				break;

    			default:
    				Log.e( TAG, "Cannot sync queried data: syncMode[" +
    						detail.getSyncMode() + "]" );
    			}

    		}

    		try {
    			ContentProviderResult[] insertResults;
    			insertResults = cr.applyBatch(Constants.AUTHORITY, insertOps );

    			if ( !validateResult( insertOps, insertResults ) ) {
    				Log.e( TAG, "Insert ops results not matching." );
    			}
    		} catch (OperationApplicationException e) {
    			Log.e( TAG, "cannot apply insert batch",  e );
    		} catch (RemoteException e) {
    			Log.e( TAG, "cannot apply insert batch",  e );
    		}

    		try {
    			ContentProviderResult[] updateResults;
    			updateResults = cr.applyBatch(Constants.AUTHORITY, updateOps );

    			if ( !validateResult( updateOps, updateResults ) ) {
    				Log.e( TAG, "Update ops results not matching." );
    			}
    		} catch (OperationApplicationException e) {
    			Log.e( TAG, "cannot apply update batch",  e );
    		} catch (RemoteException e) {
    			Log.e( TAG, "cannot apply update batch",  e );
    		}

    		try {
    			ContentProviderResult[] deleteResults;
    			deleteResults = cr.applyBatch(Constants.AUTHORITY, deleteOps );

    			if ( !validateResult( deleteOps, deleteResults ) ) {
    				Log.e( TAG, "Delete ops results not matching." );
    			}
    		} catch (OperationApplicationException e) {
    			Log.e( TAG, "cannot apply delete batch",  e );
    		} catch (RemoteException e) {
    			Log.e( TAG, "cannot apply delete batch",  e );
    		}

    	}

    	QueryTransactionInfo.getInstance().markCompleted( result );
    }

    /**
     * If number of operations does not equal number of results
     * the there has been failure with at least one of the
     * operstions.
     * 
     * @param ops The list of attempted operations.
     * @param results The list of results.
     * @return True if operation count equals result count, false otherwise.
     */
    private boolean validateResult( 
            ArrayList<ContentProviderOperation> ops,
            ContentProviderResult[] results )
    {
        if ( ops == null && results == null ) {
            return true;
        } else if ( ops == null ) {
            return false;
        } else if ( results == null ) {
            return false;
        } else return ops.size() == results.length;
    }

    /**
     * Check try_count from database against MAX_REQUEST_ATTEMPTS.
     * If try_count < MAX_REQUEST_ATTEMPTS then increment tryCount.
     * If try_count >= MAX_REQUEST_ATTEMPTS then mark transaction
     * as completed and show notification.
     *
     * @param contractData The _ID column from the table to update.
     * @param result The HttpStatus code.
     * @param allowRetry If true and less then MAX_REQUEST_ATTEMPTS
     * increment retry counter and try again later.  If false then
     * mark transaction as completed.
     */
    @SuppressLint("LogTagMismatch")
    public boolean requestFailure(DataContract contractData, int result, boolean allowRetry )
    {
        final Context context = ApplicationClass.getAppContext();
        final ContentResolver cr = context.getContentResolver();
        ContentValues values;
        MethodEnum methodEnum;
        int failedUpdateCount;
        boolean tryAgain;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.e( TAG, "processing request failure: httpResult[" + result + "]" );
        }

        Uri uri;
        uri = ContentUris.withAppendedId(
        		contractData.getCompletedUri(),
        		DataContract.getUserID() );

        Uri failedUri;
        Cursor cursor;

        failedUri = ContentUris.withAppendedId(
        		contractData.getContentUri(),
                DataContract.getUserID() );


       final String[] columns =
            { DataContract.COL_TRY_COUNT, DataContract.COL_STATUS };

        cursor = cr.query( failedUri, columns, null, null, null );
        if(cursor==null)
        	return false;

        final int colTryCount = cursor.getColumnIndex( DataContract.COL_TRY_COUNT);
        final int colStatus = cursor.getColumnIndex( DataContract.COL_STATUS);
        int tryCount;
        String status;

        if ( cursor.moveToFirst() ) {
            tryCount = cursor.getInt( colTryCount );
            status = cursor.getString( colStatus );
            methodEnum = MethodEnum.valueOf( status );
        } else {
            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG, "row has been removed by another thread" );
            }
            return false;
        }

		if(cursor !=null)
			cursor.close();

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "processing request failure: tryCount[" + tryCount + "]" );
        }

        values = new ContentValues();
        values.put( DataContract.COL_RESULT , result );

        if ( allowRetry && tryCount < Constants.MAX_REQUEST_ATTEMPTS ) {

            tryCount++;

            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG,
                        "processing request failure: set retry tryCount[" +
                        tryCount + "]" );
            }

            values.put( DataContract.COL_TRANSACTING,
                    Constants.TRANSACTION_RETRY );
            values.put( DataContract.COL_TRY_COUNT,
                    tryCount );
            failedUpdateCount = cr.update( uri, values, null, null );

            tryAgain = true;
        } else {

            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG,
                        "processing request failure: set max tryCount[" +
                        tryCount + "]" );
            }

            values.put( DataContract.COL_TRANSACTING,
                    Constants.TRANSACTION_COMPLETED );
            values.put( DataContract.COL_TRY_COUNT,
                    0 );

            failedUpdateCount = cr.update( uri, values, null, null );

            NotificationUtil.errorNotify(methodEnum);

            tryAgain = false;
        }

        return tryAgain;
    }

    /**
     * Checks QueryTransactionInfo to determine if query request
     * should be retried.  If request will not be retried
     * show notification.
     *
     * @param result The HttpStatus code.
     * @param allowRetry If true and less then MAX_REQUEST_ATTEMPTS
     * increment retry counter and try again later.  If false then
     * mark transaction as completed.
     */
    public boolean retrieveFailure( int result, boolean allowRetry )
    {
        boolean tryAgain;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "processing retrieve failure: httpResult[" + result + "]" );
        }

        if ( allowRetry ) {
            tryAgain = QueryTransactionInfo.getInstance().markRetry( result );
            if ( !tryAgain ) {
                QueryTransactionInfo.getInstance().markCompleted( result );
            }
        } else {
            QueryTransactionInfo.getInstance().markCompleted( result );
            NotificationUtil.errorNotify(MethodEnum.GET);
            tryAgain = false;
        }

        return tryAgain;
    }
 
}
