package com.basestructure.provider;

import android.content.SharedPreferences;
import android.util.Log;

import com.basestructure.base.ApplicationClass;
import com.basestructure.util.AppDateUtils;

import java.util.Calendar;

public class QueryTransactionInfo {
 
    private static String TAG = "QueryTransactionInfo";
     
    private static QueryTransactionInfo m_Instance = null;
     
    private int transacting = Constants.TRANSACTION_COMPLETED;
    private int tryCount = 0;
    private int result = 0;
	private int state=Constants.DONE;
     
    public QueryTransactionInfo() {}
     
    public static QueryTransactionInfo getInstance()
    {
        return m_Instance;
    }
    public static void setInstance( QueryTransactionInfo instance)
    {
        m_Instance=instance;
    }     
    /**
     * Mark the query request as completed. 
     * 
     * @param httpResult The HttpStatus code.
     */
    public synchronized void markCompleted( int httpResult )
    {
        transacting = Constants.TRANSACTION_COMPLETED;
        tryCount = 0;
        result = httpResult;
    }
     
    /**
     * Mark the query request as pending.
     */
    public void markPending()
    {
        if ( transacting == Constants.TRANSACTION_PENDING ) {
            return;
        }
         
        synchronized(this) {
            transacting = Constants.TRANSACTION_PENDING;
            tryCount = 0;
            result = 0;
        }
    }
     
    /**
     * Mark the query Forceful Refresh.
     */
    public void markRefresh()
    {
        if ( transacting == Constants.TRANSACTION_PENDING ) {
            return;
        }
         
        synchronized(this) {
            transacting = Constants.TRANSACTION_PENDING;
            tryCount = 0;
            result = 0;
        }
    }
     
    /**
     * If the call to the REST API fails, the query request
     * will be attempted during five sync operations before
     * it is marked as completed.  
     * 
     * @param httpResult The HttpStatus code.
     * @return True if query should be retried.
     */
    public synchronized boolean markRetry( int httpResult ) 
    {
        boolean markedRetry;
         
        tryCount++;
         
        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "retrieve.retry.httpResult[" + httpResult + "]" );
            Log.i( TAG, "retrieve.retry.tryCount[" + tryCount + "]" );
        }
         
        if ( tryCount < Constants.MAX_REQUEST_ATTEMPTS ) {
            if ( Log.isLoggable( TAG, Log.INFO) ) {
                Log.i( TAG, "retrieve.retry.RETRY" );
            }
            transacting = Constants.TRANSACTION_RETRY;
            markedRetry = true;
        } else {
            if ( Log.isLoggable( TAG, Log.INFO) ) {
                Log.i( TAG, "retrieve.retry.COMPLETE" );
            }
            transacting = Constants.TRANSACTION_COMPLETED;
            tryCount = 0;
            markedRetry = false;
        }
        result = httpResult;
         
        return markedRetry;
    }
     
    /**
     * Mark the query request as in-progress.
     */
    public synchronized void markInProgress()
    {
        transacting = Constants.TRANSACTION_IN_PROGRESS;
    }
     
    /**
     * A refresh is outstanding if:
     * - transacting = TRANSACTION_PENDING
     * - transacting = TRANSACTION_RETRY
     * - autoSync is true and data has not been refreshed in the last hour
     * 
     * @param autoSync Automatically request a sync 
     * from the REST API if data has not been refreshed 
     * in the last hour. 
     * @return True is a sync operation should be requested.
     */
    public synchronized boolean isRefreshOutstanding( boolean autoSync )
    {
        boolean refresh;
             
        if ( transacting == Constants.TRANSACTION_PENDING ||
             transacting == Constants.TRANSACTION_RETRY ) {
            refresh = true;
        } else if ( transacting == Constants.TRANSACTION_IN_PROGRESS ) {
            refresh = false;
        } else {
 
            if ( !autoSync ) {
                refresh = false;
            } else {
                // If transaction is in COMPLETED state
                // and data has not been refresh for
                // one hour a refresh will be requested
                // automatically.
                 
                SharedPreferences prefs;
                long dlMillis;
                long cutoffMillis;
     
                prefs = ApplicationClass.getAppContext().getSharedPreferences(
                        Constants.RESTFUL_PREFS, 0 );
                 
                dlMillis = prefs.getLong( Constants.PREFS_DOWNLOAD_DATE, 0 );
                cutoffMillis = AppDateUtils.addToCurrent(Calendar.HOUR_OF_DAY, -1);
                 
                if ( dlMillis <= cutoffMillis ) {
                    refresh = true;
                    transacting = Constants.TRANSACTION_PENDING;
                } else {
                    refresh = false;
                }
            }
        }
 
        return refresh;
    }
     
    @Override
    public String toString() {
        return "QueryTransactionInfo [transacting="
                + transacting + ", tryCount=" + tryCount
                + ", result=" + result + "]";
    }

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * @if forced refresh is reuired
	 */
	public boolean forcedRefresh() {
		boolean  forcedRefresh = (state==Constants.REFRESH);
		state=Constants.DONE;
		return forcedRefresh;
	}	
     
}
