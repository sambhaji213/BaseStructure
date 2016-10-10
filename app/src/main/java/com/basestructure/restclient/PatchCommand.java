package com.basestructure.restclient;

import com.basestructure.data.DataContract;
import com.basestructure.exceptions.DeviceConnectionException;
import com.basestructure.exceptions.NetworkSystemException;
import com.basestructure.exceptions.WebServiceFailedException;
import com.basestructure.processor.Processor;
import com.basestructure.provider.MethodEnum;

public class PatchCommand extends RESTCommand {

	private static final String TAG = "PatchCommand";

	private DataContract m_DataContract;
	public PatchCommand( DataContract data )
	{
		m_DataContract = data;
	}

	@Override
	protected int handleRequest(String authToken)
			throws DeviceConnectionException,
			NetworkSystemException,
			WebServiceFailedException
			{
		int statusCode = m_DataContract.handleRequest(MethodEnum.valueOf("PATCH"),authToken);
		return statusCode;
			}

	/**
	 * If attempt to update resource results in not found http status code (404)
	 * returned from the REST API, delete the same row from the local database. 
	 * Show notification on device and info message in activity.
	 */
	@Override
	public void handleNotFound()
	{
	}

	/**
	 * Delegate error handling to the Processor
	 */
	@Override
	public boolean handleError( int httpResult, boolean allowRetry )
	{
		return Processor.getInstance()
				.requestFailure( m_DataContract, httpResult, allowRetry );    
	}

}
