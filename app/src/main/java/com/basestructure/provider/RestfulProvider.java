package com.basestructure.provider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.basestructure.data.DataContract;

import java.util.ArrayList;

public class  RestfulProvider extends ContentProvider {

	private static final String TAG = "RestfulProvider";

	/** The MIME type of a directory of profile */
	private static final String CONTENT_TYPE
	= "vnd.android.cursor.dir/vnd.basestructure.profile";

	/** The MIME type of a single profile */
	private static final String CONTENT_ITEM_TYPE
	= "vnd.android.cursor.item/vnd.basestructure.profile";

	private UriMatcher uriMatcher;

	public static ArrayList<DataContract> listDataContracts = new ArrayList<DataContract>();

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile");

	public static final Uri CONTENT_URI_PROFILE_PENDING = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile/pending");
	public static final Uri CONTENT_URI_PROFILE_IN_PROGRESS = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile/in-progress");
	public static final Uri CONTENT_URI_PROFILE_COMPLETED = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile/completed");
	public static final Uri CONTENT_URI_PROFILE_QUERY_COMPLETED = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile/query-completed");
	public static final Uri CONTENT_URI_FILTERED = Uri.parse("content://"
			+ Constants.AUTHORITY + "/profile/filtered");

	@Override
	public boolean onCreate() {
		return false;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return CONTENT_TYPE;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	/**
	 * Maps to GET HTTP method.	
	 */


	private Account getAccount(Context context )
	{
		// In a real world app where there are multiple 
		// Accounts, we would allow user to choose one.

		AccountManager am = AccountManager.get( context );
		Account[] accounts;
		Account account;
		accounts = am.getAccountsByType( Constants.ACCOUNT_TYPE );
		//accounts = am.getAccountsByType( "com.sportsteamkarma" );
		if ( accounts.length > 0 ) {
			account = accounts[0];
		} else {
			throw new IllegalStateException(
					"Cannot get Account for " + Constants.ACCOUNT_TYPE );
		}

		return account;
	}

}
