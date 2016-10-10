package com.basestructure.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.basestructure.R;

// TODO: Auto-generated Javadoc

/**
 * The Class OEDialog.
 */
public class AppWaitDialog extends ProgressDialog {

	/**
	 * Instantiates a new oE dialog.
	 *
	 * @param context
	 *            the context
	 */
	public AppWaitDialog(Context context) {
		super(context);
        this.setTitle("Please wait...");
		this.setIcon(R.drawable.ic_launcher);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new oE dialog.
	 *
	 * @param context
	 *            the context
	 * @param isCancelable
	 *            the is cancelable
	 * @param message
	 *            the message
	 */
	public AppWaitDialog(Context context, boolean isCancelable, String message) {
		super(context);
		this.setIcon(R.drawable.ic_launcher);
		this.setTitle("Please wait...");
		this.setCancelable(isCancelable);
		this.setMessage(message);
		this.setCancelable(false);
	}

}
