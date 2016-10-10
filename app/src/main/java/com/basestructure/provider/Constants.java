package com.basestructure.provider;

public class Constants {
 
    public static final String ACCOUNT_SERVICE =
            "account";	
    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE =
        "com.basestructure";
    /**
     * Authtoken type string.
     */
    public static final String AUTHTOKEN_TYPE =
        "com.basestructure.auth";
 
    public static final String AUTHORITY =
            "com.basestructure.provider";
 
    public static final int MAX_REQUEST_ATTEMPTS = 5;
    public static final int NON_HTTP_FALURE = -1;
     
    public static final String RESTFUL_PREFS = "RestfulPrefs";
    public static final String PREFS_DOWNLOAD_DATE = "downloadDate";
 
    public static final int TRANSACTION_PENDING = 0;
    public static final int TRANSACTION_RETRY = 1;
    public static final int TRANSACTION_IN_PROGRESS = 2;
    public static final int TRANSACTION_COMPLETED = 3;
    public static final int REFRESH = 4;
    public static final int DONE = 5;

    public static final String POST_TEXT = "POST";
    public static final String PUT_TEXT = "PUT";
    public static final String GET_TEXT = "GET";
    public static final String DELETE_TEXT = "DELETE";
    public static final String NEW_TEXT = "NEW";
     
    public static final String KEY_ERROR_MSG = "errorMsg";
     
    public static final String ERROR_MSG_RECEIVER =
            "com.basestructure.ERROR_MSG_RECEIVER";
     
}