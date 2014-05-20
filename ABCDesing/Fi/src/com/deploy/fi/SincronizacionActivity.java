package com.deploy.fi;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.deploy.fi.services.IDataServiceResponse;
import com.deploy.fi.services.IServiceResponse;
import com.deploy.fi.services.JSON;
import com.deploy.fi.services.ServiceError;
import com.deploy.fi.services.Services;
import com.deploy.fi.services.queries.QueryLogin;
import com.deploy.fi.services.queries.QuerySynchDownload;
import com.deploy.fi.services.responses.DataSynchDownload;
import com.deploy.fi.tools.DBManager;
import com.deploy.fi.tools.SingletonConfig;

public class SincronizacionActivity extends ProgressActivity {

	private static SingletonConfig config = SingletonConfig.getInstance();

	private WifiManager.WifiLock wifilock = null;
	//private WakeLock wakeLock = null;
	
	private WakeLock wakeLock = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_sincronizacion);
		setTitle("Sincronizando ...");
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "MyWakeLock");
        wakeLock.acquire();
		
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        wifilock = manager.createWifiLock("wifilock"); 
        wifilock.acquire();
        
		initDownload();
	}

	private Services services = null;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideProgressDialog();
		
		if(wakeLock != null) wakeLock.release();
		if(wifilock != null) wifilock.release();

		if (services != null) services.cancel(true);
		services = null;
	}

	private void successService(String msg){
		if(msg != null && msg.length()>0) Log.e("successService", msg);
    	hideProgressDialog();
    	proccessJsonResponse();
	}
	
	private void errorService(String msg) {
		if(msg != null && msg.length()>0){
    		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    		Log.e("errorService", msg);
    	}
    	hideProgressDialog();
	}

	private void initDownload() {
		showProgressDialog();
		Hashtable<String, String> params = new Hashtable<String, String>();
		QuerySynchDownload query = new QuerySynchDownload(QueryLogin.TYPE_SYNCH_DOWNLOAD);
		try {
			params.put("token", config.getToken());
			query.setParams(params);

			services = new Services(SincronizacionActivity.this, query,
					new IServiceResponse() {
						@Override
						public void successResponse(IDataServiceResponse response) {
							DataSynchDownload rsp = (DataSynchDownload) response;
							int code = rsp.getCode();
							String msg = rsp.getMessage();
							if(code == 1) successService(msg);
							else errorService(msg);
						}

						@Override
						public void errorResponse(ServiceError error) {
							hideProgressDialog();
							errorService(error.getMessage());
						}
					});
			services.execute(new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private void download_() {
		showProgressDialog();

		InputStream inputStream = getResources().openRawResource(R.raw.json_synch_download);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder strBuild = new StringBuilder();
		try {
			String line;
			while ((line = reader.readLine()) != null) strBuild.append(line);
			reader.close();
		} catch (Exception e) {
		}

		String json = strBuild.toString();
		
	}*/
	
	/*
	 * This method check and inserts synch data with a database
	 * */
	private void proccessJsonResponse(){
		DBManager db = new DBManager(this);
		db.open();
		db.backupDatabase();
		db.close();

		hideProgressDialog();
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("result", 1);
	    setResult(Activity.RESULT_OK, resultIntent);
	    finish();
	}
	
}
