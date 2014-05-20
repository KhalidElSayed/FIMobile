package com.deploy.fi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;

/**
 * Class to manage native ProgressDialog
 **/
public class ProgressFragmentActivity extends FragmentActivity {
	
	private ProgressDialog progressDialog = null;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(progressDialog != null) progressDialog = null;
	}
	
	/**
	 * Show the ProgressDialog
	 **/
	protected final void showProgressDialog() {
		if (this.progressDialog == null) {
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setIndeterminate(true);
			this.progressDialog.setCancelable(false);
			this.progressDialog.setCanceledOnTouchOutside(false);
		}
		this.progressDialog.setMessage(getResources().getString(R.string.progress_message));
		this.progressDialog.show();
	}
	
	/**
	 * Hide the ProgressDialog
	 **/
	protected final void hideProgressDialog() {
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
		}
	}
}
