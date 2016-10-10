package com.basestructure.provider;

public enum SyncMode{ 
	U(0), I(1),D(2);

	private int statusCode;

	SyncMode(int i) {
		statusCode = i;
	}

	public int valueOf(){ return statusCode; }
}