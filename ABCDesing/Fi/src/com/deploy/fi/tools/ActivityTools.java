package com.deploy.fi.tools;

import android.app.Activity;
import android.content.Intent;

/**
 * This is a class with tools related with activitys
 **/
public class ActivityTools {

	/**
	 * This method closes an activity 
	 **/
	public static void waitForActivity(Activity context, Intent intent, int requestCode){
		try{
			context.startActivityForResult(intent, requestCode);
        }catch(Exception e){}
	}
	
	/**
	 * This method closes an activity 
	 **/
	public static void close(Activity context){
		try{
			context.finish();
        }catch(Exception e){}
	}
	
	/**
	 * This method closes all stack activitys and launch one new 
	 **/
	public static void closeAllAndLaunch(Activity context, Class<?> cls){
		try{
			Intent intent = new Intent(context, cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        context.startActivity(intent);
	        context.finish();
        }catch(Exception e){}
	}
	
	/**
	 * This method closes all stack activitys until a root activity and launch one new 
	 **/
	public static void closeToRootAndLaunchOne(Activity context, Class<?> cls){
		try{
			Intent loginIntent = new Intent(context, cls);
	        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        context.startActivity(loginIntent);
	        context.finish();
        }catch(Exception e){}
	}
	
	/**
	 * This method launch one activity 
	 **/
	public static void launchOne(Activity context, Class<?> cls){
		try{
			Intent loginIntent = new Intent(context, cls);
	        context.startActivity(loginIntent);
        }catch(Exception e){}
	}
	
}

