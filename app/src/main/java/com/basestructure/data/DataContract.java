package com.basestructure.data;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.basestructure.exceptions.DeviceConnectionException;
import com.basestructure.exceptions.NetworkSystemException;
import com.basestructure.exceptions.WebServiceFailedException;
import com.basestructure.provider.MethodEnum;
import com.basestructure.provider.QueryTransactionInfo;
import com.basestructure.provider.SyncMode;
import com.basestructure.restclient.RESTCommand;
import com.basestructure.syncadapter.NetworkUtilities;
import com.basestructure.util.AppLoginSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class DataContract {

	private static final String TAG = "DataContract";

	public static final String APP_STRING_SPLITTER= "APP_STRING_SPLITTER";

	private static String USER_ID = "";
	private static String SUPER_USER_ID = "";
	private static String SUPER_USER_PROFILE_ID = "";
	private static String SUPER_USER_NAME = "";

	public static final String PARAM_FULL_IMAGE = "cfLargeImageUrl";
	public static final String PARAM_THUMBS_IMAGE = "cfThumbImageUrl";

	public static  final String ClassName  = "className";

	public static  final String _ID  = "_id";
	public static  final String COL_TRANSACTING ="transacting";
	public static  final String COL_TRANS_DATE="transdate";
	public static  final String COL_RESULT ="result";
	public static  final String COL_TRY_COUNT ="trycount";
	public static  final String COL_STATUS  = "status";

	public static final String DEFAULT_SORT_ORDER ="status";

	public static final int U=0;
	public static final int I=1;
	public static final int D=2;

	int 	m_statusCode=200;
	long 	m_date=0;
	int 	m_syncMode=SyncMode.I.valueOf();

	public HashMap<String, SyncableObject> syncableItems  = new HashMap<String,  SyncableObject>();

	public static final boolean isEditable() {
		return StringUtils.equals(getSuperUserIDString(), getUserIDString());
	}

	public static void setSuperUserID(String userID){
		SUPER_USER_ID=userID;
	}

	public static void setSuperProfileID(String profileId){
		SUPER_USER_PROFILE_ID=profileId;
	}

	public static long getSuperUserProfileID() {
		String profileId = getSuperUserProfileIDString();
		if( StringUtils.isBlank(profileId) )
			return 0l;
		return Long.parseLong(profileId, 10);
	}

	public static long getSuperUserID() {
		String userId = getSuperUserIDString();
		if( StringUtils.isBlank(userId) )
			return 0l;
		return Long.parseLong(userId, 10);
	}

	public String getDefaultID() {
		return getUserIDString();
	}

	public static String getSuperUserIDString() {
		if(StringUtils.isBlank(SUPER_USER_ID)){
			SUPER_USER_ID  = AppLoginSession.getSuperUserId();
		}
		return  SUPER_USER_ID;
	}

	public static String getSuperUserProfileIDString() {
		if(StringUtils.isBlank(SUPER_USER_PROFILE_ID)){
			SUPER_USER_PROFILE_ID  = AppLoginSession.getSuperUserProfileId();
		}
		return  SUPER_USER_PROFILE_ID;
	}

	public static void setSuperUserName(String superUserName) {
		SUPER_USER_NAME=superUserName;
	}

	public static String getSuperUserName() {
		return  SUPER_USER_NAME;
	}

	public static String getUserIDString() {
		if(USER_ID.isEmpty())
			return getSuperUserIDString();
		return  USER_ID;
	}

	public static long getUserID() {
		return Long.parseLong(getUserIDString(), 10);
	}

	public static void setUserID(String userID){
		USER_ID=userID;
		if( StringUtils.isEmpty(userID))
			USER_ID=getSuperUserIDString();
	}

	public QueryTransactionInfo getInstance(){
		return null;
	}
	public static DataContract getContractData() {
		return null;
	}

	public Uri getPatchUri() {
		return null;
	}

	public Uri getContentUri() {
		return null;
	}
	public Uri getPendingUri() {
		return null;
	}
	public Uri getCompletedUri() {
		return null;
	}
	public Uri getInProgressUri() {
		return null;
	}
	public Uri getQueryCompletedUri() {
		return null;
	}
	public Uri getFiltereUri() {
		return null;
	}

	public ContentProviderOperation getUpdateOperation(Uri pendingUri){
		return null;
	}
	public ContentProviderOperation getInsertOperation(){
		return null;
	}

	public ContentValues getContentValues(){
		return null;
	}

	public Uri getStatusUpdateUri() {
		return null;
	}

	public int delete(
			SQLiteDatabase db,
			ContentResolver cr,
			Account account,
			Uri uri,
			String selection,
			String[] selectionArgs
	) 	{
		return -1;
	}

	public Cursor query(
			SQLiteDatabase db,
			SQLiteQueryBuilder qb,
			ContentResolver cr,
			Account account,
			Uri uri,
			String[] projection,
			String selection,
			String[] selectionArgs,
			String sortOrder
	){
		return null;
	}

	public Uri insert(
			SQLiteDatabase db,
			ContentResolver cr,
			Account ac,
			Uri uri,
			ContentValues values
	){
		return null;
	}

	public int update(
			SQLiteDatabase db,
			ContentResolver cr,
			Account account,
			Uri uri,
			ContentValues values,
			String selection,
			String[] selectionArgs){
		return -1;
	}

	public void setHttpResult(int statusCode) {
		m_statusCode=statusCode;

	}
	public int getHttpResult() {
		return m_statusCode;
	}
	public void setTransDate(long transDate) {
		m_date= transDate;
	}


	public long getTransDate() {
		return m_date;
	}

	public int getSyncMode() {
		return m_syncMode;
	}

	public void setSyncMode(SyncMode syncVal) {
		m_syncMode=syncVal.valueOf();
	}

	public DataContract[] handleRequest(
			JSONObject jsonObject )
			throws WebServiceFailedException {
		return null;
	}

	public Bundle getBundle(){
		return null;
	}

	public int handleRequest(MethodEnum methodEnum, String authToken )
			throws DeviceConnectionException,
			NetworkSystemException,
            WebServiceFailedException
	{
		return -1;
	}

	public int loadLoginData(  MethodEnum methodEnum, String authToken )
			throws DeviceConnectionException,
			NetworkSystemException,
			WebServiceFailedException
	{
		return -1;
	}

	static public String makePlaceholders(int len, String placeholder, String delim) {
		if (len < 1) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder(len * 2 - 1);
			sb.append(placeholder);
			for (int i = 1; i < len; i++) {
				sb.append(delim+placeholder);
			}
			return sb.toString();
		}
	}

	public  static String[] splitStringIntoStrings(String inString)
	{
		return StringUtils.splitByWholeSeparator(inString, APP_STRING_SPLITTER);
	}

	public static String buildItemItemString(String[] strArray, String delim) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strArray.length; ++i) {
			sb.append(delim);
			sb.append(strArray[i]);
		}
		String outStr=sb.toString();
		outStr= StringUtils.removeStart(outStr, delim);
		outStr= StringUtils.removeEnd(outStr, delim);
		return  outStr;
	}

	public Boolean call()
	{
		return false;
	}

	public static String getImageUrl(String imageUrlLink, String imageType )
	{
		String fullImageUrl="";
		String thumbImageUrl="";

		HttpResponse resp=null;
		HttpGet get = NetworkUtilities.getHttpGet(imageUrlLink);
		RESTCommand.createHttpClient();
		try {
			resp = RESTCommand.mHttpClient.execute(get);
		} catch (IOException e) {
			String msg = "GET method failed: Cannot connect to network.";
			Log.i(TAG, msg, e);
		}

		if(resp==null)
			return "";

		int statusCode = resp.getStatusLine().getStatusCode();

		if ( Log.isLoggable( TAG, Log.INFO ) ) {
			Log.i( TAG, "HTTP runImagePraser statusCode[" + statusCode + "]" );
		}

		String respText="";
		if ( statusCode == HttpStatus.SC_OK ) {

			try {
				respText = EntityUtils.toString(resp.getEntity());
			} catch ( IOException e ) {
				String msg = "GET method failed: Invalid response.";
				Log.e(TAG, msg, e);
			}

			try {
				JSONObject jObj = new JSONObject( respText );
				fullImageUrl = jObj.getString( PARAM_FULL_IMAGE );
				thumbImageUrl = jObj.getString( PARAM_THUMBS_IMAGE );
			}catch ( JSONException e ) {
				String msg =
						"GET method failed: Cannot parse data returned from web service.";
				Log.e( TAG, msg );
				//throw new WebServiceFailedException( msg );
			}
		}

		if(imageType==PARAM_FULL_IMAGE)
			return fullImageUrl;
		return thumbImageUrl;

	}
}