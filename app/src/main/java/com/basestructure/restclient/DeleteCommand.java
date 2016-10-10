package com.basestructure.restclient;

import com.basestructure.data.DataContract;
import com.basestructure.exceptions.DeviceConnectionException;
import com.basestructure.exceptions.NetworkSystemException;
import com.basestructure.exceptions.WebServiceFailedException;
import com.basestructure.processor.Processor;
import com.basestructure.provider.MethodEnum;

public class DeleteCommand extends RESTCommand {
 
    private static final String TAG = "DeleteCommand";
     
    private DataContract m_DataContract;
    public DeleteCommand( DataContract data )
    {
        this.m_DataContract = data;
    }
     
    @Override
    protected int handleRequest( String authToken )
        throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException
    {
        int statusCode = m_DataContract.handleRequest(MethodEnum.valueOf("DELETE"),"test");
        return statusCode;
    }
 
    /**
     * If attempt to delete resource results in not found http status code (404)
     * returned from the REST API, delete the same row from the local database. 
     */
    @Override
    public void handleNotFound()
    {
    	//TODO
       // Processor.getInstance().delete( requestId );
    }
 
    /**
     * Delegate error handling to the Processor
     */
    @Override
    public boolean handleError(int httpResult, boolean allowRetry) 
    {
        return Processor.getInstance().requestFailure(m_DataContract, httpResult, allowRetry );
    }
 
}
