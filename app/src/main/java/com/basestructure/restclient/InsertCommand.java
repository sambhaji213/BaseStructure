package com.basestructure.restclient;

import com.basestructure.data.DataContract;
import com.basestructure.exceptions.DeviceConnectionException;
import com.basestructure.exceptions.NetworkSystemException;
import com.basestructure.exceptions.WebServiceFailedException;
import com.basestructure.processor.Processor;

public class InsertCommand extends RESTCommand {
 
    private static final String TAG = "InsertCommand";
     
    
    private DataContract dataContract;
    public InsertCommand( DataContract data )
    {
        this.dataContract = data;
    }
    
    @Override
    protected int handleRequest( String authToken )
        throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException
    {
    	return 200;
    }
 
    /**
     * Delegate error handling to the Processor.
     */
    public boolean handleError( int httpResult, boolean allowRetry )
    {
        return Processor.getInstance()
                .requestFailure( dataContract, httpResult, allowRetry );    
    }
 
}

