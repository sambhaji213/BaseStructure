package com.basestructure.util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;

import org.json.JSONArray;

public class AppJsonArrayRequest extends com.android.volley.toolbox.JsonArrayRequest {
	public static final int HX_DEFAULT_MAX_RETRIES=3;
	public static final int DEFAULT_TIMEOUT_MS=6000;
	public static final int DEFAULT_BACKOFF_MULTI=2;

	public AppJsonArrayRequest(int method, String url, JSONArray jsonRequest,
							   Listener<JSONArray> listener, ErrorListener errorListener) {
		super(method, url,jsonRequest, listener,errorListener);
	}

	@Override
	public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
		return super.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
				HX_DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULTI));
	}
}
