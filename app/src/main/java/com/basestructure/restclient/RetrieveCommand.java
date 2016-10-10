package com.basestructure.restclient;

import android.util.Log;

import com.basestructure.data.DataContract;
import com.basestructure.exceptions.DeviceConnectionException;
import com.basestructure.exceptions.NetworkSystemException;
import com.basestructure.exceptions.WebServiceFailedException;
import com.basestructure.processor.Processor;
import com.basestructure.provider.MethodEnum;

public class RetrieveCommand extends RESTCommand {
 
	DataContract m_DataContract;
	public RetrieveCommand(DataContract dataContract){
		m_DataContract=dataContract;
	}
	
    private static final String TAG = "RetrieveCommand";
 
    @Override
    protected int handleRequest(String authToken )
        throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException
    {
        if ( Log.isLoggable( TAG, Log.INFO )) {
            Log.i( TAG, "executing RESTMethod.GET" );
        }
        int statusCode = m_DataContract.handleRequest(MethodEnum.valueOf("GET"), authToken);
        return statusCode;
    }
 
    /**
     * Delegate error handling to the Processor.
     */
    @Override
    public boolean handleError(int httpResult, boolean allowRetry) 
    {
        return Processor.getInstance()
                .retrieveFailure( httpResult, allowRetry );    
    }
 
}
