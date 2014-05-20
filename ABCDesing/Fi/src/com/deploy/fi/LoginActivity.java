package com.deploy.fi;

import java.text.DecimalFormat;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deploy.fi.services.IDataServiceResponse;
import com.deploy.fi.services.IServiceResponse;
import com.deploy.fi.services.ServiceError;
import com.deploy.fi.services.Services;
import com.deploy.fi.services.queries.QueryLogin;
import com.deploy.fi.services.responses.DataLoginResponse;
import com.deploy.fi.tools.ActivityTools;
import com.deploy.fi.tools.FileManager;
import com.deploy.fi.tools.Hash;
import com.deploy.fi.tools.SingletonConfig;

public class LoginActivity extends ProgressActivity {

	private static SingletonConfig config = SingletonConfig.getInstance();
	
	public static String formatNumber(double d){
		String pettern = "###,###,###.##";  
	    DecimalFormat myFormatter = new DecimalFormat(pettern);  
	    return myFormatter.format(d);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
        wakeLock.release();
        
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        WifiManager.WifiLock wifilock = manager.createWifiLock("wifilock"); 
        wifilock.acquire();
        wifilock.release();*/
        
        setTitle("Formas Intimas");
        
        if(getToken()){
        	ActivityTools.closeAllAndLaunch(this, DashboardActivity.class);
        	finish();
        }
        else{
        	setContentView(R.layout.activity_login);
        	GUI();
        }
    }
    
    private boolean waitAction = false;
    private Services services = null; 
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(services != null){
    		services.cancel(true);
    		services = null;
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	waitAction = false;
    	hideProgressDialog();
    }
    
    private void successService(String msg, String token){
    	Log.e("successService", msg);
    	hideProgressDialog();
    	waitAction = false;
    	setToken(token);
    	ActivityTools.closeAllAndLaunch(this, DashboardActivity.class);
    }
    
    public static void removeToken(Context context){
    	try{
    	}catch(Exception e){}
    }
    
    public static void deleteToken(Activity activity){
    	String configPath = FileManager.fixPath("config");
    	if(FileManager.existFolder(activity, configPath)){
    		String fileLogin = "login.cfg";
    		if(FileManager.existFile(activity, configPath, fileLogin)) FileManager.deleteFile(activity, configPath, fileLogin);
    	}
    }
    
    private boolean getToken(){
    	boolean readed = false; 
    	String configPath = FileManager.fixPath("config");
    	if(FileManager.existFolder(this, configPath)){
    		String fileLogin = "login.cfg";
    		if(FileManager.existFile(this, configPath, fileLogin)) {
    			String readToken = FileManager.readFile(this, configPath, fileLogin);
    			if(readToken != null && readToken.length()>0) {
    				config.setToken(readToken);
    				readed = true;
    			}
    		}
    	}
    	return readed;
    }
    
    private void setToken(String token){
    	String configPath = FileManager.fixPath("config");
    	if(!FileManager.existFolder(this, configPath)) FileManager.createFolder(this, configPath);
    	String fileLogin = "login.cfg";
    	if(FileManager.existFile(this, configPath, fileLogin)) FileManager.deleteFile(this, configPath, fileLogin);
    	if(!FileManager.existFile(this, configPath, fileLogin)) FileManager.createFile(this, configPath, fileLogin, token);
    	config.setToken(token);
    }
    
    private void errorService(String msg){
    	if(msg != null && msg.length()>0){
    		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    		Log.e("errorService", msg);
    	}
    	hideProgressDialog();
    	waitAction = false;
    }
    
    private void GUI(){
    	final EditText txtUsuario = (EditText) findViewById(R.id.txtUsuario);
    	final EditText txtClave = (EditText) findViewById(R.id.txtClave);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!waitAction){
					String user = txtUsuario.getText().toString().trim();
					String pass = txtClave.getText().toString().trim();
					if(user != null && user.length()>0 && pass != null && pass.length()>0){
						showProgressDialog();
						Hashtable<String, String> params = new Hashtable<String, String>();
						pass = Hash.getDefaultDigest(pass);
						waitAction = true;
						
						params.put("user", user);
						params.put("pass", pass);
						
						QueryLogin query = new QueryLogin(QueryLogin.TYPE_LOGGIN);
						query.setParams(params);
						
						services = new Services(LoginActivity.this, query, new IServiceResponse(){
							@Override
							public void successResponse(IDataServiceResponse response) {
								DataLoginResponse rsp =(DataLoginResponse)response;
								String msg = rsp.getMessage();
								int code = rsp.getCode();
								if(code == 1) successService(msg, rsp.getToken());
								else errorService(msg);
							}
							@Override
							public void errorResponse(ServiceError error) {
								hideProgressDialog();
								errorService(error.getMessage());
							}
						});
						services.execute(new Object[]{});
					}
				}
			}
		});
    	
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    
}
